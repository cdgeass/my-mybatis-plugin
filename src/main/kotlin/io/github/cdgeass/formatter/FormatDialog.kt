package io.github.cdgeass.formatter

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import io.github.cdgeass.PluginBundle
import java.awt.Dimension
import javax.swing.JComponent

/**
 * @author cdgeass
 * @since  2021-03-09
 */
class FormatDialog(
    private val project: Project,
    private val selectedText: String
) : DialogWrapper(project, true, IdeModalityType.IDE) {

    init {
        init()
        title = PluginBundle.message("formatter.dialog.title")
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                component(
                    editorTextField(selectedText, Dimension(500, 400))
                        .format(project, selectedText)
                )
            }
        }
    }

}