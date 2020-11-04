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

    private val contextDefaultModelTypeComboBox: ComboBox<String> = ComboBox(arrayOf("conditional", "flat", "hierarchical"))
    private val contextTargetRuntimeComboBox: ComboBox<String> = ComboBox(arrayOf("MyBatis3DynamicSql",
            "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"))
    private val contextPropertiesTableModel: PropertiesTableModel = PropertiesTableModel(arrayOf("autoDelimitKeywords",
            "beginningDelimiter", "endingDelimiter", "javaFileEncoding", "javaFormatter", "targetJava8", "kotlinFileEncoding",
            "kotlinFormatter", "xmlFormatter"))

    private val javaTypeResolverForceBigDecimalsCheckBox: JBCheckBox = JBCheckBox("forceBigDecimals")
    private val javaTypeResolverUseJSR310TypesCheckBox: JBCheckBox = JBCheckBox("useJSR310Types")

    init {
        val contextPropertiesTable = TableView(contextPropertiesTableModel).let {
            it.emptyText.text = "There has no properties"
            it
        }
        val contextToolbarDecorator = ToolbarDecorator.createDecorator(contextPropertiesTable)
                .setAddAction {
                    contextPropertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    contextPropertiesTableModel.removeRow(contextPropertiesTable.selectedRow)
                }

        val contextPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("DefaultModelType:"), contextDefaultModelTypeComboBox)
                .addLabeledComponent(JBLabel("TargetRuntime:"), contextTargetRuntimeComboBox)
                .addComponent(TitledSeparator("Properties"))
                .addComponent(contextToolbarDecorator.createPanel())
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val javaTypeResolverPanel = FormBuilder.createFormBuilder()
                .addComponent(javaTypeResolverForceBigDecimalsCheckBox)
                .addComponent(javaTypeResolverUseJSR310TypesCheckBox)
                .addComponentFillVertically(JPanel(), 0)
                .panel
                .let {
                    it.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    it
                }

        val tabbedPaneWrapper = TabbedPaneWrapper(Disposer.newDisposable())
        tabbedPaneWrapper.addTab("Context", contextPanel)
        tabbedPaneWrapper.addTab("JavaTypeResolver", javaTypeResolverPanel)

        mainPanel = tabbedPaneWrapper.component
    }

    fun getComponent(): JComponent {
        return mainPanel
    }
}