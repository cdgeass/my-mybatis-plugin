package io.github.cdgeass.formatter

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.EditorKind
import io.github.cdgeass.PluginBundle
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * @author cdgeass
 * @since 2022-03-28
 */
class FormatAndCopyAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedText = e.getData(PlatformDataKeys.EDITOR)?.selectionModel?.selectedText ?: return

        val formattedText = format(selectedText).joinToString("\n")
        Toolkit.getDefaultToolkit().systemClipboard.setContents(
            StringSelection(formattedText),
            null
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.text = PluginBundle.message("formatter.action.copy.text")
        e.presentation.description = PluginBundle.message("formatter.action.copy.description")

        val editor = e.getData(PlatformDataKeys.EDITOR) ?: let {
            e.presentation.isEnabledAndVisible = false
            return
        }
        if (editor.editorKind != EditorKind.CONSOLE) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        val selectedText = editor.selectionModel.selectedText ?: let {
            e.presentation.isEnabledAndVisible = false
            return
        }

        e.presentation.isEnabled = canFormat(selectedText)
    }

}