package io.github.cdgeass.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since 2020-09-25
 */
class MyBatisGeneratorSettingsComponent {

    private val mainPanel: JPanel

    private val contextDefaultModelTypeComboBox: ComboBox<String> = ComboBox(arrayOf("conditional", "flat", "hierarchical"))
    private val contextTargetRuntimeComboBox: ComboBox<String> = ComboBox(arrayOf("MyBatis3DynamicSql",
            "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"))
    private val contextPropertiesModel: PropertiesTableModel = PropertiesTableModel()

    init {

        val contextPanel = FormBuilder.createFormBuilder()
                .addComponent(TitledSeparator("Context"))
                .addLabeledComponent(JBLabel("DefaultModelType:"), contextDefaultModelTypeComboBox)
                .addLabeledComponent(JBLabel("TargetRuntime:"), contextTargetRuntimeComboBox)
                .addComponentFillVertically(JPanel(), 0)
                .panel

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(contextPanel)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }

    fun getPanel(): JPanel {
        return mainPanel
    }
}