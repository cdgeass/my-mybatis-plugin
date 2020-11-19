package io.github.cdgeass.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
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
class TablePanel : JPanel(BorderLayout()) {

    private val enableInsertCheckBox = JBCheckBox("EnableInsert")

    private val enableSelectByPrimaryKeyCheckBox = JBCheckBox("EnableSelectByPrimaryKey")

    private val enableSelectByExampleCheckBox = JBCheckBox("EnableSelectByExample")

    private val enableUpdateByPrimaryKeyCheckBox = JBCheckBox("EnableUpdateByPrimaryKey")

    private val enableDeleteByPrimaryKeyCheckBox = JBCheckBox("EnableDeleteByPrimaryKey")

    private val enableDeleteByExampleCheckBox = JBCheckBox("EnableDeleteByExample")

    private val enableCountByExampleCheckBox = JBCheckBox("EnableCountByExample")

    private val enableUpdateByExampleCheckBox = JBCheckBox("EnableUpdateByExample")

    private val selectByPrimaryKeyQueryIdCheckBox = JBCheckBox("SelectByPrimaryKeyQueryId")

    private val selectByExampleQueryIdCheckBox = JBCheckBox("SelectByExampleQueryId")

    private val modelTypeComboBox = ComboBox(arrayOf("Conditional", "Flat", "Hierarchical"))

    private val modelEscapeWildcardsCheckBox = JBCheckBox("EscapeWildcards")

    private val delimitIdentifiersCheckBox = JBCheckBox("DelimitIdentifiers")

    private val delimitAllColumnsCheckBox = JBCheckBox("DelimitAllColumns")

    private val propertiesTableModel = PropertiesTableModel(mutableListOf("ConstructorBased", "IgnoreQualifiersAtRuntime",
            "Immutable", "ModelOnly", "RootClass", "RootInterface", "RuntimeCatalog", "RuntimeSchema", "RuntimeTableName",
            "SelectAllOrderByClause", "UseActualColumnNames", "UseColumnIndexes", "UseCompoundPropertyNames"))

    init {
        val propertiesTable = TableView(propertiesTableModel)
        val propertiesToolbarDecorator = ToolbarDecorator.createDecorator(propertiesTable)
                .setAddAction {
                    propertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    propertiesTableModel.removeRow(propertiesTable.selectedRow)
                }
        this.add(
                FormBuilder.createFormBuilder()
                        .addLabeledComponent("ModelType:", modelTypeComboBox)
                        .addComponent(enableInsertCheckBox)
                        .addComponent(enableSelectByPrimaryKeyCheckBox)
                        .addComponent(enableSelectByExampleCheckBox)
                        .addComponent(enableUpdateByPrimaryKeyCheckBox)
                        .addComponent(enableDeleteByPrimaryKeyCheckBox)
                        .addComponent(enableDeleteByExampleCheckBox)
                        .addComponent(enableCountByExampleCheckBox)
                        .addComponent(enableUpdateByExampleCheckBox)
                        .addComponent(selectByPrimaryKeyQueryIdCheckBox)
                        .addComponent(selectByExampleQueryIdCheckBox)
                        .addComponent(modelEscapeWildcardsCheckBox)
                        .addComponent(delimitIdentifiersCheckBox)
                        .addComponent(delimitAllColumnsCheckBox)
                        .addComponent(TitledSeparator("Properties"))
                        .addComponent(propertiesToolbarDecorator.createPanel())
                        .panel
        )
    }

    fun isEnableInsert(): Boolean {
        return enableInsertCheckBox.isSelected
    }

    fun isEnableSelectByPrimaryKey(): Boolean {
        return enableSelectByPrimaryKeyCheckBox.isSelected
    }

    fun isEnableSelectByExample(): Boolean {
        return enableSelectByExampleCheckBox.isSelected
    }

    fun isEnableUpdateByPrimaryKey(): Boolean {
        return enableUpdateByPrimaryKeyCheckBox.isSelected
    }

    fun isEnableDeleteByPrimaryKey(): Boolean {
        return enableDeleteByPrimaryKeyCheckBox.isSelected
    }

    fun isEnableDeleteByExample(): Boolean {
        return enableDeleteByExampleCheckBox.isSelected
    }

    fun isEnableCountByExample(): Boolean {
        return enableCountByExampleCheckBox.isSelected
    }

    fun isEnableUpdateByExample(): Boolean {
        return enableUpdateByExampleCheckBox.isSelected
    }

    fun isSelectByPrimaryKeyQueryId(): Boolean {
        return selectByPrimaryKeyQueryIdCheckBox.isSelected
    }

    fun isSelectByExampleQueryId(): Boolean {
        return selectByExampleQueryIdCheckBox.isSelected
    }

    fun getModelType(): String {
        return modelTypeComboBox.item
    }

    fun isModelEscapeWildcards(): Boolean {
        return modelEscapeWildcardsCheckBox.isSelected
    }

    fun isDelimitIdentifiers(): Boolean {
        return delimitIdentifiersCheckBox.isSelected
    }

    fun isDelimitAllColumns(): Boolean {
        return delimitAllColumnsCheckBox.isSelected
    }

    fun getProperties(): List<MutablePair<String, String>> {
        return propertiesTableModel.items.filter { StringUtils.isNotBlank(it.left) && StringUtils.isNotBlank(it.right) }
    }
}