package io.github.cdgeass.component

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since 2020-11-06
 */
class SqlMapGeneratorPanel : JPanel(BorderLayout()) {

    private val enableSubPackagesCheckBox = JBCheckBox("EnableSubPackages")

    init {
        this.add(
                FormBuilder.createFormBuilder()
                        .addComponent(enableSubPackagesCheckBox)
                        .addComponentFillVertically(JPanel(), 0)
                        .panel
        )
    }

    fun isEnableSubPackages(): Boolean {
        return enableSubPackagesCheckBox.isSelected
    }
}