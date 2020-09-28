package io.github.cdgeass.component

import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since 2020-09-25
 */
class MyBatisGeneratorSettingsComponent {

    private val mainPanel: JPanel

    init {
        mainPanel = FormBuilder.createFormBuilder()
                .panel
    }

    fun getPanel(): JPanel {
        return mainPanel
    }
}