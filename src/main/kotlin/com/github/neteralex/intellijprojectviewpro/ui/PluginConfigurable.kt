package com.github.neteralex.intellijprojectviewpro.ui

import com.github.neteralex.intellijprojectviewpro.model.CompressRule
import com.github.neteralex.intellijprojectviewpro.services.SettingService
import com.github.neteralex.intellijprojectviewpro.utils.createPredicate
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.options.Configurable.NoScroll
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel

class PluginConfigurable(project: Project) : BoundSearchableConfigurable(
    helpTopic = "ProjectViewPro",
    _id = "ProjectViewPro",
    displayName = "ProjectViewPro"
), NoScroll {
    companion object {
        const val ID = "com.github.neteralex.intellijprojectviewpro.ui.PluginConfigurable"
    }

    private val settings = project.service<SettingService>()
    private val propertyGraph = PropertyGraph()
    private val settingsProperty = propertyGraph.lazyProperty { SettingService().apply { copyFrom(settings) } }
    private val foldingEnabledPredicate = settingsProperty.createPredicate(SettingService::isCompressingEnabled)
    private val hideAllGroupsPredicate = settingsProperty.createPredicate(SettingService::isHidingAllGroups)
    private val compressRuleProperty = propertyGraph
        .lazyProperty<CompressRule?> { null }
        .apply { afterChange { ApplicationManager.getApplication().invokeLater {} } }

    override fun createPanel(): DialogPanel {
        TODO("Not yet implemented")
    }

}