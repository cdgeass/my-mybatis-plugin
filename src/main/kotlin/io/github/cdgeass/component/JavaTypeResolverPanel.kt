package io.github.cdgeass.component

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since  2020-11-06
 */
class JavaTypeResolverPanel : JPanel(BorderLayout()) {

    private val forceBigDecimalsCheckBox = JBCheckBox("ForceBigDecimals")

    private val useJSR310TypesCheckBox = JBCheckBox("UseJSR310Types")

    init {
        this.add(
                FormBuilder.createFormBuilder()
                        .addComponent(forceBigDecimalsCheckBox)
                        .addComponent(useJSR310TypesCheckBox)
                        .addComponentFillVertically(JPanel(), 0)
                        .panel
        )
    }

    fun isForceBigDecimals(): Boolean {
        return forceBigDecimalsCheckBox.isSelected
    }

    fun isUseJSR310Types(): Boolean {
        return useJSR310TypesCheckBox.isSelected
    }
}