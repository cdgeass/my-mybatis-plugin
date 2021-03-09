package io.github.cdgeass.formatter

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
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
        title = "Format Sql"
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                component(editorTextField(project, selectedText))
            }
        }
    }

}