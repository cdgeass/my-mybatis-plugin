package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.layout.panel
import com.intellij.ui.table.JBTable
import io.github.cdgeass.PluginBundle
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

/**
 * @author cdgeass
 * @since 2020-09-25
 */
class SettingsConfigurable(project: Project) : BoundConfigurable("MyBatis Generator") {

    private val settings = project.getService(Settings::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                row(PluginBundle.message("generator.settings.sourceFolder")) {
                    textField(settings::sourceDir)
                }
                row(PluginBundle.message("generator.settings.resourcesFolder")) {
                    textField(settings::resourcesDir)
                }
                row(PluginBundle.message("generator.settings.modelNamePrefixPattern")) {
                    textField(settings::modelNamePrefixPattern)
                }
                row(PluginBundle.message("generator.settings.modelNameSuffixPattern")) {
                    textField(settings::modelNameSuffixPattern)
                }
                row(PluginBundle.message("generator.settings.modelNameFormat")) {
                    textField(settings::modelNameFormat)
                }
                row(PluginBundle.message("generator.settings.clientNameFormat")) {
                    textField(settings::clientNameFormat)
                }
            }
            titledRow(PluginBundle.message("generator.settings.schemaPackage")) {
                row {
                    panel("", table(), false)
                }
                noteRow(PluginBundle.message("generator.settings.schemaPackage.note"))
            }
            titledRow(PluginBundle.message("generator.settings.plugins")) {
                row {
                    checkBox(PluginBundle.message("generator.settings.plugins.lombok"), settings::enableLombok)
                    checkBox(PluginBundle.message("generator.settings.plugins.generic"), settings::enableGeneric)
                }
            }
        }
    }

    fun table(): JPanel {
        val tableModel = DefaultTableModel(null, arrayOf("Schema", "Model", "client"))

        val schemaPackages = settings.schemaPackages
        if (schemaPackages.isNotEmpty()) {
            schemaPackages.forEach { (dataSource, packages) ->
                tableModel.addRow(arrayOf(dataSource, packages.keys.first(), packages.values.first()))
            }
        }

        val jbTable = JBTable(tableModel)
        tableModel.addTableModelListener { e ->
            val changedSchemaPackages = mutableMapOf<String, Map<String, String>>()
            var row = 0
            while (row < tableModel.rowCount) {
                if (
                    !(tableModel.getValueAt(row, 0) as String?).isNullOrBlank() &&
                    !(tableModel.getValueAt(row, 1) as String?).isNullOrBlank() &&
                    !(tableModel.getValueAt(row, 2) as String?).isNullOrBlank()
                ) {
                    changedSchemaPackages[tableModel.getValueAt(row, 0) as String] =
                        mapOf(
                            Pair(
                                tableModel.getValueAt(row, 1) as String,
                                tableModel.getValueAt(row, 2) as String
                            )
                        )
                }
                row++
            }
            settings.schemaPackages = changedSchemaPackages
        }

        return ToolbarDecorator.createDecorator(jbTable)
            .setAddAction {
                tableModel.addRow(emptyArray())
            }
            .setRemoveAction {
                val selectedRow = jbTable.selectedRow
                tableModel.removeRow(selectedRow)
            }
            .createPanel()
    }
}
