package com.github.neteralex.intellijprojectviewpro.psi

import com.github.neteralex.intellijprojectviewpro.services.SettingService
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vcs.changes.ignore.cache.PatternCache
import com.intellij.openapi.vcs.changes.ignore.lang.Syntax
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import kotlin.io.path.relativeToOrNull

class SearchScope(
    project: Project,
    private val pattern: String,
    private val settings: SettingService = project.service<SettingService>()
) : GlobalSearchScope(project) {

    private val patternCache = PatternCache.getInstance(project)
    private val patterns = pattern
        .applySettings()
        .split(' ')
        .filter(String::isNotBlank)
        .mapNotNull {
            patternCache.createPattern(it, Syntax.GLOB)
        }

    override fun isSearchInModuleContent(aModule: Module) = true

    override fun isSearchInLibraries() = false

    override fun getDisplayName() = pattern
    override fun contains(file: VirtualFile): Boolean {
        if (patterns.isEmpty()) {
            return false
        }
        val base = project?.guessProjectDir()?.toNioPath() ?: return false
        val relativePath = file.toNioPath().relativeToOrNull(base) ?: return false
        val path = relativePath.toString().applySettings()

        return patterns.any {
            it.matcher(path).matches()
        }
    }

    override fun toString() = pattern

    private fun String.applySettings() = when (settings.isNameCaseSensitive) {
        true -> this
        false -> lowercase()
    }

}