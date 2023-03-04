package io.github.cdgeass.formatter

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.sql.psi.SqlLanguage
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import io.github.cdgeass.PluginBundle
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * @author cdgeass
 * @since 2021-03-11
 */
class FormatToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager

        val factory = contentManager.factory

        val formatContent = createFormatContent(project, factory)

        contentManager.addContent(formatContent)
    }

    private fun createFormatContent(project: Project, factory: ContentFactory): Content {
        val unformattedEditorTextField = editorTextField(dimension = Dimension(500, 500))
        val formattedEditorTextField = editorTextField(SqlLanguage.INSTANCE, project, dimension = Dimension(500, 500))

        return factory.createContent(
            panel {
                row {
                    label(PluginBundle.message("formatter.toolwindow.unformatted"))
                }
                row {
                    cell(unformattedEditorTextField)
                }
                row {
                    button(PluginBundle.message("formatter.toolwindow.clean")) {
                        unformattedEditorTextField.clean()
                        formattedEditorTextField.clean()
                    }
                    button(PluginBundle.message("formatter.toolwindow.copy")) {
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(
                            StringSelection(formattedEditorTextField.text),
                            null
                        )
                        Messages.showInfoMessage(
                            PluginBundle.message("formatter.toolwindow.copy.success"),
                            PluginBundle.message("title"),
                        )
                    }
                    button(PluginBundle.message("formatter.toolwindow.format")) {
                        formattedEditorTextField.format(project, unformattedEditorTextField.text)
                    }
                }
                row {
                    label(PluginBundle.message("formatter.toolwindow.formatted"))
                }
                row {
                    cell(formattedEditorTextField)
                }
            },
            PluginBundle.message("formatter.toolwindow.title"),
            false
        )
    }
}
