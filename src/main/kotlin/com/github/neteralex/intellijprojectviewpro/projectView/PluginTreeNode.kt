package com.github.neteralex.intellijprojectviewpro.projectView

import com.github.neteralex.intellijprojectviewpro.model.CompressRule
import com.github.neteralex.intellijprojectviewpro.psi.PluginSearchScope
import com.github.neteralex.intellijprojectviewpro.services.SettingService
import com.intellij.icons.AllIcons.General.CollapseComponent
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileSystemItemFilter
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN

class PluginTreeNode(
    project: Project,
    private val viewSettings: ViewSettings?,
    private val settings: SettingService,
    private val compressRule: CompressRule,
    private val parent: PsiDirectoryNode,
) : ProjectViewNode<String>(project, compressRule.name, viewSettings), PsiFileSystemItemFilter,
    PsiElementProcessor<PsiFileSystemItem> {
    val containsMatchedChildKey: Key<Boolean> = Key.create("CONTAINS_MATCHED_CHILD")
    val ruleScope = PluginSearchScope(project, compressRule.pattern, settings)

    override fun update(presentation: PresentationData) {
        presentation.apply {
            val textAttributes = SimpleTextAttributes(STYLE_PLAIN, compressRule.itemForeground)
            addText(ColoredFragment(compressRule.name, compressRule.pattern, textAttributes))
            setIcon(CollapseComponent)
        }
    }

    override fun getName() = compressRule.name

    override fun toString() = name

    override fun execute(item: PsiFileSystemItem): Boolean {
        val matched = item.matches()
        if (matched) {
            item.parent?.putUserData(containsMatchedChildKey, matched)
        }
        return !matched
    }

    override fun shouldShow(item: PsiFileSystemItem): Boolean {
        return item.matches()
    }

    override fun computeBackgroundColor() = compressRule.itemBackground

    override fun getChildren(): MutableCollection<AbstractTreeNode<*>> =
        ProjectViewDirectoryHelper
            .getInstance(myProject)
            .getDirectoryChildren(parent.value, viewSettings, true, this)

    override fun contains(file: VirtualFile) = children.firstOrNull {
        it is ProjectViewNode && it.virtualFile == file
    } != null

    private fun PsiFileSystemItem.matches(): Boolean {
        return ruleScope.contains(virtualFile) || !processChildren(this@PluginTreeNode)
    }
}