package io.github.cdgeass.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Disposer
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.FormBuilder
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.tuple.MutablePair
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since  2020-11-06
 */
class ContextPanel : JPanel(BorderLayout()) {

    private val defaultModelTypeComboBox = ComboBox(arrayOf("conditional", "flat", "hierarchical"))

    private val targetRuntimeComboBox = ComboBox(arrayOf("MyBatis3DynamicSql",
            "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"))

    private val properties = mutableListOf("autoDelimitKeywords", "beginningDelimiter", "endingDelimiter", "javaFileEncoding",
            "javaFormatter", "targetJava8", "kotlinFileEncoding", "kotlinFormatter", "xmlFormatter")

    private val propertiesTableModel = PropertiesTableModel(properties)

    private val javaTypeResolverPanel = JavaTypeResolverPanel()
    private val javaModelGeneratorPanel = JavaModelGeneratorPanel()
    private val sqlMapGeneratorPanel = SqlMapGeneratorPanel()
    private val javaClientGeneratorPanel = JavaClientGeneratorPanel()
    private val tablePanel = TablePanel()

    init {
        val propertiesTable = TableView(propertiesTableModel)
        this.add(FormBuilder.createFormBuilder()
                .addComponent(TitledSeparator("Context"))
                .addLabeledComponent("DefaultModelType:", defaultModelTypeComboBox)
                .addLabeledComponent("TargetRuntime:", targetRuntimeComboBox)
                .addComponent(
                        ToolbarDecorator.createDecorator(propertiesTable)
                                .setAddAction {
                                    propertiesTableModel.addRow(MutablePair.of("", ""))
                                }
                                .setRemoveAction {
                                    propertiesTableModel.removeRow(propertiesTable.selectedColumn)
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

    fun getProperties(): List<MutablePair<String, String>> {
        return propertiesTableModel.items.filter { StringUtils.isNotBlank(it.left) && StringUtils.isNotBlank(it.right) }
    }
}