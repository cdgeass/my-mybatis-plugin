package io.github.cdgeass.generator.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Disposer
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.FormBuilder
import org.apache.commons.lang3.tuple.MutablePair
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since  2020-11-06
 */
class ContextPanel : JPanel(BorderLayout()) {

    private val defaultModelTypeComboBox = ComboBox(arrayOf("conditional", "flat", "hierarchical"))

    private val targetRuntimeComboBox = ComboBox(
            arrayOf(
                    "MyBatis3DynamicSql",
                    "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"
            )
    )

    private val properties = linkedMapOf(
            Pair("autoDelimitKeywords", Boolean::class.java),
            Pair("beginningDelimiter", String::class.java),
            Pair("endingDelimiter", String::class.java),
            Pair("javaFileEncoding", String::class.java),
//        Pair("javaFormatter", String::class.java),
            Pair("targetJava8", Boolean::class.java),
            Pair("kotlinFileEncoding", String::class.java)
//        Pair("kotlinFormatter", String::class.java),
//        Pair("xmlFormatter", String::class.java)
    )

    private val propertiesTableModel = PropertiesTableModel(properties)

    private val javaTypeResolverPanel = JavaTypeResolverPanel()
    private val javaModelGeneratorPanel = JavaModelGeneratorPanel()
    private val sqlMapGeneratorPanel = SqlMapGeneratorPanel()
    private val javaClientGeneratorPanel = JavaClientGeneratorPanel()
    private val tablePanel = TablePanel()
    private val commentGeneratorPanel = CommentGeneratorPanel()

    init {
        val propertiesTable = TableView(propertiesTableModel)
        this.add(
                FormBuilder.createFormBuilder()
                        .addComponent(TitledSeparator("Context"))
                        .addLabeledComponent("DefaultModelType:", defaultModelTypeComboBox)
                        .addLabeledComponent("TargetRuntime:", targetRuntimeComboBox)
                        .addComponent(
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
                        .addComponent(
                                TabbedPaneWrapper(Disposer.newDisposable())
                                        .apply {
                                            this.addTab("JavaTypeResolver", javaTypeResolverPanel)
                                            this.addTab("JavaModelGenerator", javaModelGeneratorPanel)
                                            this.addTab("SqlMapGenerator", sqlMapGeneratorPanel)
                                            this.addTab("JavaClientGenerator", javaClientGeneratorPanel)
                                            this.addTab("Table", tablePanel)
                                            this.addTab("CommentGenerator", commentGeneratorPanel)
                                        }
                                        .component
                        )
                        .addComponentFillVertically(JPanel(), 0)
                        .panel, BorderLayout.CENTER)
    }

    fun getDefaultModelType(): String {
        return defaultModelTypeComboBox.item
    }

    fun getTargetRuntime(): String {
        return targetRuntimeComboBox.item
    }

    fun getProperties(): Map<String, String> {
        return propertiesTableModel.items.associateBy({ it.left }, { it.right })
    }

    fun getJavaTypeResolver(): JavaTypeResolverPanel {
        return javaTypeResolverPanel
    }

    fun getJavaModelGenerator(): JavaModelGeneratorPanel {
        return javaModelGeneratorPanel
    }

    fun getSqlMapGenerator(): SqlMapGeneratorPanel {
        return sqlMapGeneratorPanel
    }

    fun getJavaClientGenerator(): JavaClientGeneratorPanel {
        return javaClientGeneratorPanel
    }

    fun getTable(): TablePanel {
        return tablePanel
    }

    fun getCommentGenerator(): CommentGeneratorPanel {
        return commentGeneratorPanel
    }

    fun setDefaultModelType(defaultModelType: String): ContextPanel {
        defaultModelTypeComboBox.item = defaultModelType
        return this
    }

    fun setTargetRuntime(targetRuntime: String): ContextPanel {
        targetRuntimeComboBox.item = targetRuntime
        return this
    }

    fun setProperties(properties: Map<String, String>): ContextPanel {
        if (properties.isNotEmpty()) {
            propertiesTableModel.addRows(properties.map { (property, value) -> MutablePair.of(property, value) })
        }
        return this
    }

}