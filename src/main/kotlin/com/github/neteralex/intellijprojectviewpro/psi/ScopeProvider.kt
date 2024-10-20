package com.github.neteralex.intellijprojectviewpro.psi

import com.github.neteralex.intellijprojectviewpro.PluginBundle
import com.github.neteralex.intellijprojectviewpro.services.SettingService

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.search.SearchScopeProvider

class ScopeProvider : SearchScopeProvider {
    override fun getDisplayName() = PluginBundle.getMessage("projectViewPro.name")
    override fun getSearchScopes(project: Project, dataContext: DataContext): MutableList<PluginSearchScope?> {
        val settings = project.service<SettingService>()
        return settings.compressRules.map {
            PluginSearchScope(
                project,
                it.pattern
            )
        }.toMutableList()
    }
}