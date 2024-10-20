package com.github.neteralex.intellijprojectviewpro.ui.components

import com.github.neteralex.intellijprojectviewpro.PluginBundle.message
import com.github.neteralex.intellijprojectviewpro.model.CompressRule
import com.github.neteralex.intellijprojectviewpro.utils.bindColor
import com.github.neteralex.intellijprojectviewpro.utils.bindColorControl
import com.github.neteralex.intellijprojectviewpro.utils.bindText
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.util.isNotNull
import com.intellij.openapi.options.UiDslUnnamedConfigurable
import com.intellij.ui.ColorPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.selected
import com.intellij.ui.layout.ComponentPredicate

class CompressRuleEditor(val ruleProperty: ObservableMutableProperty<CompressRule?>) :
    UiDslUnnamedConfigurable.Simple() {
    private lateinit var backgroundCheckBox: Cell<JBCheckBox>
    private lateinit var backgroundColorPanel: Cell<ColorPanel>
    private lateinit var foregroundCheckBox: Cell<JBCheckBox>
    private lateinit var foregroundColorPanel: Cell<ColorPanel>
    private lateinit var nameTextField: Cell<JBTextField>
    private lateinit var patternTextField: Cell<ExpandableTextField>

    private val selectedRowPredicate = object : ComponentPredicate() {
        override fun invoke() = ruleProperty.isNotNull().get()
        override fun addListener(listener: (Boolean) -> Unit) =
            ruleProperty.afterChange { listener(it != null) }
    }

    override fun Panel.createContent() {
        rowsRange {
            row(message("projectViewPro.settings.name")) {
                nameTextField = textField()
                    .align(Align.FILL)
                    .bindText(ruleProperty, CompressRule::name)

            }
            row(message("projectViewPro.settings.compressRules")) {
                patternTextField = expandableTextField()
                    .align(Align.FILL)
                    .bindText(ruleProperty, CompressRule::pattern)
            }
            row {
                foregroundCheckBox = checkBox(message("projectViewPro.settings.itemForeground"))
                    .bindColorControl(ruleProperty, CompressRule::itemForeground, JBColor.foreground().brighter())

                foregroundColorPanel = cell(ColorPanel())
                    .align(Align.FILL)
                    .enabledIf(foregroundCheckBox.selected)
                    .bindColor(ruleProperty, CompressRule::itemForeground)
            }
            row {
                backgroundCheckBox = checkBox(message("projectViewPro.settings.itemBackground"))
                    .bindColorControl(ruleProperty, CompressRule::itemBackground, JBColor.background().darker())

                backgroundColorPanel = cell(ColorPanel())
                    .align(Align.FILL)
                    .enabledIf(backgroundCheckBox.selected)
                    .bindColor(ruleProperty, CompressRule::itemBackground)
            }
        }.enabledIf(selectedRowPredicate)
    }

}