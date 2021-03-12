package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.layout.panel
import com.intellij.ui.table.TableView
import io.github.cdgeass.generator.ui.PropertiesTable
import io.github.cdgeass.generator.ui.PropertiesTableModel

/**
 * @author cdgeass
 * @since  2021-01-26
 */
class CommentGeneratorConfigurable(project: Project) : BoundConfigurable("CommentGenerator") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("suppressAllComments", Boolean::class.java),
            Pair("suppressDate", Boolean::class.java),
            Pair("addRemarkComments", Boolean::class.java),
            Pair("dateFormat", String::class.java)
        )
    }

    private var commentGenerator = CommentGenerator.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, commentGenerator::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }
}