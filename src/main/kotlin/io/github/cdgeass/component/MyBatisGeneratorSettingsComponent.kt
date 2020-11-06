package io.github.cdgeass.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Disposer
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.table.TableView
import com.intellij.util.ui.FormBuilder
import org.apache.commons.lang3.tuple.MutablePair
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since 2020-09-25
 */
class MyBatisGeneratorSettingsComponent {

    private val mainPanel: JComponent

    private val contextDefaultModelTypeComboBox = ComboBox(arrayOf("conditional", "flat", "hierarchical"))
    private val contextTargetRuntimeComboBox = ComboBox(arrayOf("MyBatis3DynamicSql",
            "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"))
    private val contextPropertiesTableModel = PropertiesTableModel(arrayOf("autoDelimitKeywords",
            "beginningDelimiter", "endingDelimiter", "javaFileEncoding", "javaFormatter", "targetJava8", "kotlinFileEncoding",
            "kotlinFormatter", "xmlFormatter"))

    private val javaTypeResolverForceBigDecimalsCheckBox = JBCheckBox("forceBigDecimals")
    private val javaTypeResolverUseJSR310TypesCheckBox = JBCheckBox("useJSR310Types")

    private val javaModelGeneratorPropertiesTableModel = PropertiesTableModel(arrayOf("constructorBased",
            "enableSubPackages", "exampleTargetPackage", "exampleTargetProject", "immutable", "rootClass", "trimStrings"))

    private val sqlMapGeneratorEnableSubPackagesCheckBox = JBCheckBox("enableSubPackages")

    private val javaClientGeneratorTypeComboBox = ComboBox(arrayOf("ANNOTATEDMAPPER", "MIXEDMAPPER",
            "XMLMAPPER"))
    private val javaClientGeneratorPropertiesTableModel = PropertiesTableModel(arrayOf("enableSubPackages", "rootInterface",
            "useLegacyBuilder"))

    private val tableEnableInsertCheckBox = JBCheckBox("enableInsert")
    private val tableEnableSelectByPrimaryKeyCheckBox = JBCheckBox("enableSelectByPrimaryKey")
    private val tableEnableSelectByExampleCheckBox = JBCheckBox("enableSelectByExample")
    private val tableEnableUpdateByPrimaryKeyCheckBox = JBCheckBox("enableUpdateByPrimaryKey")
    private val tableEnableDeleteByPrimaryKeyCheckBox = JBCheckBox("enableDeleteByPrimaryKey")
    private val tableEnableDeleteByExampleCheckBox = JBCheckBox("enableDeleteByExample")
    private val tableEnableCountByExampleCheckBox = JBCheckBox("enableCountByExample")
    private val tableEnableUpdateByExampleCheckBox = JBCheckBox("enableUpdateByExample")
    private val tableSelectByPrimaryKeyQueryIdCheckBox = JBCheckBox("selectByPrimaryKeyQueryId")
    private val tableSelectByExampleQueryIdCheckBox = JBCheckBox("selectByExampleQueryId")
    private val tableModelTypeComboBox = ComboBox(arrayOf("conditional", "flat", "hierarchical"))
    private val tableModelEscapeWildcardsCheckBox = JBCheckBox("escapeWildcards")
    private val tableDelimitIdentifiersCheckBox = JBCheckBox("delimitIdentifiers")
    private val tableDelimitAllColumnsCheckBox = JBCheckBox("delimitAllColumns")
    private val tablePropertiesTableModel = PropertiesTableModel(arrayOf("constructorBased", "ignoreQualifiersAtRuntime",
            "immutable", "modelOnly", "rootClass", "rootInterface", "runtimeCatalog", "runtimeSchema", "runtimeTableName",
            "selectAllOrderByClause", "useActualColumnNames", "useColumnIndexes", "useCompoundPropertyNames"))

    init {
        val javaTypeResolverPanel = FormBuilder.createFormBuilder()
                .addComponent(javaTypeResolverForceBigDecimalsCheckBox)
                .addComponent(javaTypeResolverUseJSR310TypesCheckBox)
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val javaModelGeneratorPropertiesTable = TableView(javaModelGeneratorPropertiesTableModel)
        val javaModelGeneratorPropertiesToolbarDecorator = ToolbarDecorator.createDecorator(javaModelGeneratorPropertiesTable)
                .setAddAction {
                    javaModelGeneratorPropertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    javaModelGeneratorPropertiesTableModel.removeRow(javaModelGeneratorPropertiesTable.selectedRow)
                }
        val javaModelGeneratorPanel = FormBuilder.createFormBuilder()
                .addComponent(TitledSeparator("Properties"))
                .addComponent(javaModelGeneratorPropertiesToolbarDecorator.createPanel())
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val sqlMapGeneratorPanel = FormBuilder.createFormBuilder()
                .addComponent(sqlMapGeneratorEnableSubPackagesCheckBox)
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val javaClientGeneratorPropertiesTable = TableView(javaClientGeneratorPropertiesTableModel)
        val javaClientGeneratorToolbarDecorator = ToolbarDecorator.createDecorator(javaClientGeneratorPropertiesTable)
                .setAddAction {
                    javaClientGeneratorPropertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    javaClientGeneratorPropertiesTableModel.removeRow(javaClientGeneratorPropertiesTable.selectedRow)
                }
        val javaClientGeneratorPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("type", javaClientGeneratorTypeComboBox)
                .addComponent(TitledSeparator("Properties"))
                .addComponent(javaClientGeneratorToolbarDecorator.createPanel())
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val tablePropertiesTable = TableView(tablePropertiesTableModel)
        val tablePropertiesToolbarDecorator = ToolbarDecorator.createDecorator(tablePropertiesTable)
                .setAddAction {
                    tablePropertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    tablePropertiesTableModel.removeRow(tablePropertiesTable.selectedRow)
                }
        val tablePanel = FormBuilder.createFormBuilder()
                .addComponent(tableEnableInsertCheckBox)
                .addComponent(tableEnableSelectByPrimaryKeyCheckBox)
                .addComponent(tableEnableSelectByExampleCheckBox)
                .addComponent(tableEnableUpdateByPrimaryKeyCheckBox)
                .addComponent(tableEnableDeleteByPrimaryKeyCheckBox)
                .addComponent(tableEnableDeleteByExampleCheckBox)
                .addComponent(tableEnableCountByExampleCheckBox)
                .addComponent(tableEnableUpdateByExampleCheckBox)
                .addComponent(tableSelectByPrimaryKeyQueryIdCheckBox)
                .addComponent(tableSelectByExampleQueryIdCheckBox)
                .addLabeledComponent("modelType:", tableModelTypeComboBox)
                .addComponent(tableModelEscapeWildcardsCheckBox)
                .addComponent(tableDelimitIdentifiersCheckBox)
                .addComponent(tableDelimitAllColumnsCheckBox)
                .addComponent(TitledSeparator("Properties"))
                .addComponent(tablePropertiesToolbarDecorator.createPanel())
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val tabbedPaneWrapper = TabbedPaneWrapper(Disposer.newDisposable())
                .let {
                    it.addTab("JavaTypeResolver", javaTypeResolverPanel)
                    it.addTab("JavaModelGenerator", javaModelGeneratorPanel)
                    it.addTab("SqlMapGenerator", sqlMapGeneratorPanel)
                    it.addTab("JavaClientGenerator", javaClientGeneratorPanel)
                    it.addTab("Table", tablePanel)
                    it
                }

        val contextPropertiesTable = TableView(contextPropertiesTableModel)
        val contextPropertiesToolbarDecorator = ToolbarDecorator.createDecorator(contextPropertiesTable)
                .setAddAction {
                    contextPropertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    contextPropertiesTableModel.removeRow(contextPropertiesTable.selectedRow)
                }

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(TitledSeparator("Context"))
                .addLabeledComponent(JBLabel("DefaultModelType:"), contextDefaultModelTypeComboBox)
                .addLabeledComponent(JBLabel("TargetRuntime:"), contextTargetRuntimeComboBox)
                .addComponent(TitledSeparator("Properties"))
                .addComponent(contextPropertiesToolbarDecorator.createPanel())
                .addComponent(TitledSeparator("Sub Settings"))
                .addComponent(tabbedPaneWrapper.component)
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }
    }

    fun getComponent(): JComponent {
        return mainPanel
    }
}