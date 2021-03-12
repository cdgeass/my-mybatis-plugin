package io.github.cdgeass.formatter

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.EditorKind
import io.github.cdgeass.PluginBundle

/**
 * @author cdgeass
 * @since 2021-03-10
 */
class FormatAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val selectedText = e.getData(PlatformDataKeys.EDITOR)?.selectionModel?.selectedText ?: return
        FormatDialog(project, selectedText).show()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.text = PluginBundle.message("formatter.action.text")
        e.presentation.description = PluginBundle.message("formatter.action.description")

        val editor = e.getData(PlatformDataKeys.EDITOR) ?: let {
            e.presentation.isVisible = false
            e.presentation.isEnabled = false
            return
        }
        if (editor.editorKind != EditorKind.CONSOLE) {
            e.presentation.isVisible = false
            e.presentation.isEnabled = false
            return
        }

        val selectedText = editor.selectionModel.selectedText ?: let {
            e.presentation.isVisible = false
            e.presentation.isEnabled = false
            return
        }

        e.presentation.isEnabled = canFormat(selectedText)
    }
}