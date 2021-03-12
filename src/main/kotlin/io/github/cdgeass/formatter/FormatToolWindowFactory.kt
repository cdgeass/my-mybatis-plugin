package io.github.cdgeass.formatter

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.sql.psi.SqlLanguage
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.layout.panel
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
        val unformattedEditorTextField = editorTextField(dimension = Dimension(500, 500), editable = true)
        val formattedEditorTextField = editorTextField(SqlLanguage.INSTANCE, project, dimension = Dimension(500, 500))

        return factory.createContent(
            panel {
                row {
                    component(unformattedEditorTextField).constraints(growX)
                }
                row {
                    button(PluginBundle.message("formatter.toolwindow.clean")) {
                        unformattedEditorTextField.clean()
                        formattedEditorTextField.clean()
                    }.constraints(growX)
                    button(PluginBundle.message("formatter.toolwindow.copy")) {
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(
                            StringSelection(formattedEditorTextField.text),
                            null
                        )
                    }.constraints(growX)
                    button(PluginBundle.message("formatter.toolwindow.format")) {
                        formattedEditorTextField.format(project, unformattedEditorTextField.text)
                    }.constraints(pushX)
                }
                row {
                    component(formattedEditorTextField).constraints(growX)
                }
            },
            PluginBundle.message("formatter.toolwindow.title"),
            false
        )
    }

}