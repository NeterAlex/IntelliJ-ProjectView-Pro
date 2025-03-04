package com.github.neteralex.intellijprojectviewpro.projectView

import com.github.neteralex.intellijprojectviewpro.listeners.SettingListener
import com.github.neteralex.intellijprojectviewpro.services.SettingService
import com.github.neteralex.intellijprojectviewpro.utils.or
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.ProjectViewPane
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vcs.FileStatusListener
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vcs.changes.ignore.cache.PatternCache
import com.intellij.openapi.vcs.changes.ignore.lang.Syntax

class PluginTreeStructureProvider(private val project: Project) : TreeStructureProvider {

    private val settings by lazy { project.service<SettingService>() }
    private val patternCache = PatternCache.getInstance(project)
    private var previewProjectViewPane: ProjectViewPane? = null
    private var previewGraphProperty: ObservableMutableProperty<SettingService>? = null
    private val state get() = previewGraphProperty?.get() ?: settings

    init {
        project.messageBus
            .connect(project)
            .subscribe(SettingListener.TOPIC, SettingListener {
                refreshProjectView()
            })

        FileStatusManager.getInstance(project).addFileStatusListener(object : FileStatusListener {
            override fun fileStatusesChanged() {
                if (state.isCompressingEnabled) {
                    refreshProjectView()
                }
            }
        }, project)
    }

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        viewSettings: ViewSettings?,
    ): Collection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children
        val foldingGroup = parent.foldingFolder

        return when {
            !state.isCompressingEnabled -> children
            parent !is PsiDirectoryNode -> children
            !isModule(parent, project) -> children

            else -> {
                val matched = mutableSetOf<AbstractTreeNode<*>>()
                val folders = state.compressRules.mapNotNull { rule ->
                    (children - matched)
                        .match(rule.pattern)
                        .also { matched.addAll(it) }
                        .takeUnless { state.isHidingAllGroups || (state.isHidingEmptyGroup && matched.isEmpty()) }
                        ?.run {
                            matched.addAll(this)
                            PluginTreeNode(project, viewSettings, state, rule, parent)
                        }
                }

                children - matched + folders
            }
        }
    }

    fun withProjectViewPane(projectViewPane: ProjectViewPane) = apply {
        previewProjectViewPane = projectViewPane
    }

    fun withState(property: ObservableMutableProperty<SettingService>) = apply {
        previewGraphProperty = property.also {
            it.afterChange {
                refreshProjectView()
            }
        }
    }

    private fun isModule(node: PsiDirectoryNode, project: Project) =
        node.virtualFile
            ?.let { ModuleUtil.findModuleForFile(it, project)?.guessModuleDir() == it } == true

    private fun Collection<AbstractTreeNode<*>>.match(patterns: String) = this
        .filter {
            when (it) {
                is PsiFileNode -> true
                else -> false
            }
        }
        .filter {
            when (it) {
                is ProjectViewNode -> it.virtualFile?.name ?: it.name
                else -> it.name
            }.let { name ->
                patterns
                    .split(' ')
                    .any { pattern ->
                        patternCache
                            .createPattern(pattern, Syntax.GLOB)
                            ?.matcher(name)
                            ?.matches() == true
                    }
            }
        }

    private fun refreshProjectView() = previewProjectViewPane
        .or { ProjectView.getInstance(project).currentProjectViewPane }
        ?.updateFromRoot(true)

    private val <T> AbstractTreeNode<T>.isFolded: Boolean
        get() = parent?.run { this is PluginTreeNode || isFolded } ?: false

    private val <T> AbstractTreeNode<T>.foldingFolder: AbstractTreeNode<*>?
        get() = parent.takeIf { it is PluginTreeNode } ?: parent?.foldingFolder
}