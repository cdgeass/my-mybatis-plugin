package io.github.cdgeass.formatter

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.sql.psi.SqlLanguage
import com.intellij.ui.EditorTextField
import com.intellij.ui.dsl.builder.panel
import io.github.cdgeass.PluginBundle
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

/**
 * @author cdgeass
 * @since 2021-03-09
 */
class FormatDialog(
    private val project: Project,
    private val selectedText: String,
    private val editorTextField: EditorTextField = editorTextField(
        SqlLanguage.INSTANCE,
        project,
        selectedText,
        Dimension(500, 400)
    ).format(project, selectedText)
) : DialogWrapper(project, true, IdeModalityType.IDE) {

    init {
        init()
        title = PluginBundle.message("formatter.dialog.title")
        myOKAction.putValue(Action.NAME, PluginBundle.message("formatter.dialog.copy"))
        myCancelAction.putValue(Action.NAME, PluginBundle.message("formatter.dialog.cancel"))
    }

    override fun createDefaultActions() {
        super.createDefaultActions()

        myOKAction = object : OkAction() {
            override fun actionPerformed(e: ActionEvent?) {
                Toolkit.getDefaultToolkit().systemClipboard.setContents(
                    StringSelection(editorTextField.text),
                    null
                )
                Messages.showInfoMessage(
                    PluginBundle.message("formatter.dialog.copy.success"),
                    PluginBundle.message("title"),
                )
            }
        }
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                cell(editorTextField)
            }
        }
    }
}
