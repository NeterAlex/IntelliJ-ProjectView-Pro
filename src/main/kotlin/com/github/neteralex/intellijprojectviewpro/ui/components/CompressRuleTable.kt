package com.github.neteralex.intellijprojectviewpro.ui.components

import com.github.neteralex.intellijprojectviewpro.PluginBundle.message
import com.github.neteralex.intellijprojectviewpro.constant.GlobalConstants
import com.github.neteralex.intellijprojectviewpro.model.CompressRule
import com.github.neteralex.intellijprojectviewpro.services.SettingService
import com.intellij.execution.util.ListTableWithButtons
import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.util.transform
import com.intellij.ui.ToolbarDecorator
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ListTableModel
import java.awt.Component
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableCellRenderer

class CompressRuleTable(
    private val settingsProperty: ObservableMutableProperty<SettingService>
) : ListTableWithButtons<CompressRule>() {
    private val compressRulesProperty = settingsProperty.transform({ it.compressRules }, {
        settingsProperty.get().apply {
            with(compressRules) {
                clear()
                addAll(it)
            }
        }
    })

    init {
        tableView.apply {
            columnSelectionAllowed = false
            tableHeader.reorderingAllowed = false
            columnModel.getColumn(0).apply {
                maxWidth = JBUI.scale(24)
                minWidth = JBUI.scale(24)
            }
            model.addTableModelListener { compressRulesProperty.set(elements) }
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            setShowGrid(false)
            setValues(compressRulesProperty.get())
        }
        compressRulesProperty.afterChange { refreshValues() }
    }

    override fun createToolbarDecorator(): ToolbarDecorator? {
        return ToolbarDecorator
            .createDecorator(tableView, null)
            .setToolbarPosition(ActionToolbarPosition.RIGHT)
            .setPanelBorder(JBUI.Borders.empty())
    }

    override fun createListModel() = ListTableModel<CompressRule>(ColorsColumn(), NameColumn(), CompressRulesColumn())
    override fun createElement() = CompressRule()
    override fun isUpDownSupported() = true
    override fun shouldEditRowOnCreation() = false
    override fun addNewElement(newElement: CompressRule?) {
        super.addNewElement(newElement)
        SwingUtilities.invokeLater({ tableView.selection = listOf(newElement) })
    }

    override fun isEmpty(p0: CompressRule?) = p0?.pattern.isBlank()
    override fun cloneElement(p0: CompressRule?) = p0?.copy()
    override fun canDeleteElement(p0: CompressRule?) = true

    private class ColorsColumn : ColumnInfo<CompressRule, String>(message("projectViewPro.settings.color")) {

        override fun getName() = ""

        override fun valueOf(item: CompressRule?) = GlobalConstants.COLOR_PREVIEW_DEFAULT_TEXT

        override fun getRenderer(item: CompressRule?) = object : DefaultTableCellRenderer() {

            override fun getTableCellRendererComponent(
                table: JTable?,
                value: Any?,
                isSelected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ): Component {
                val hasNoColor = item?.itemForeground == null && item?.itemBackground == null
                return super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected && hasNoColor,
                    hasFocus,
                    row,
                    column
                )
                    .apply {
                        foreground = item?.itemForeground
                        background = item?.itemBackground
                    }
            }
        }
    }

    private class NameColumn : ColumnInfo<CompressRule, String>(message("projectViewPro.settings.name")) {
        override fun valueOf(item: CompressRule?) = item?.name
    }

    private class CompressRulesColumn :
        ColumnInfo<CompressRule, String>(message("projectViewPro.settings.compressRules")) {
        override fun valueOf(item: CompressRule?) = item?.pattern
    }
}