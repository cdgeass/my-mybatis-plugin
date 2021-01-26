package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.layout.LCFlags
import com.intellij.ui.layout.panel
import com.intellij.ui.table.TableView
import io.github.cdgeass.generator.component.PropertiesTableModel
import javax.swing.DefaultComboBoxModel

/**
 * @author cdgeass
 * @since  2021-01-26
 */
internal val DEFAULT_MODEL_TYPE = arrayOf("conditional", "flat", "hierarchical")
internal val TARGET_RUNTIME = arrayOf(
    "MyBatis3DynamicSql", "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"
)
internal val propertiesTableModel = PropertiesTableModel(
    linkedMapOf(
        Pair("autoDelimitKeywords", Boolean::class.java),
        Pair("beginningDelimiter", String::class.java),
        Pair("endingDelimiter", String::class.java),
        Pair("javaFileEncoding", String::class.java),
        Pair("targetJava8", Boolean::class.java),
        Pair("kotlinFileEncoding", String::class.java)
    )
)

class ContextConfigurable(private val project: Project) : BoundConfigurable("Context") {

    private val context get() = Context.getInstance(project)

    override fun createPanel(): DialogPanel {
        val context = Context.getInstance(project)
        val state = context.state

        val propertiesTable = TableView(propertiesTableModel)
        return panel(LCFlags.fillX) {
            row("DefaultModelType:") {
                comboBox(
                    DefaultComboBoxModel(DEFAULT_MODEL_TYPE),
                    context::defaultModelType
                )
            }
            row("TargetRuntime:") {
                comboBox(
                    DefaultComboBoxModel(TARGET_RUNTIME),
                    context::targetRuntime
                )
            }
            row("Properties:") {
                row {
                    component(
                        ToolbarDecorator.createDecorator(propertiesTable)
                            .setAddAction {
                                propertiesTableModel.addRow()
                            }
                            .setRemoveAction {
                                val selectedRows = propertiesTable.selectedRows
                                if (selectedRows.isNotEmpty()) {
                                    propertiesTableModel.removeRow(selectedRows[selectedRows.size - 1])
                                }
                            }
                            .createPanel()
                    )
                }
            }
        }
    }
}
