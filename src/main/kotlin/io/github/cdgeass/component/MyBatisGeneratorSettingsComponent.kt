package io.github.cdgeass.component

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since 2020-09-25
 */
class MyBatisGeneratorSettingsComponent {

    private val mainPanel: JPanel
    private val sourceText: JBTextField = JBTextField()
    private val resourceText: JBTextField = JBTextField()

    init {
        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Source Folder"), sourceText, false)
                .addLabeledComponent(JBLabel("Resource Folder"), resourceText, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }

    fun getPanel(): JPanel {
        return mainPanel
    }

    fun getSourceFolder(): String {
        return sourceText.text
    }

    fun setSourceFolder(sourceFolder: String) {
        sourceText.text = sourceFolder
    }

    fun getResourceFolder(): String {
        return resourceText.text
    }

    fun setResourceFolder(resourceFolder: String) {
        resourceText.text = resourceFolder
    }
}