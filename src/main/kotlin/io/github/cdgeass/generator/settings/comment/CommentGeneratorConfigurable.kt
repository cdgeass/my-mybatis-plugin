package io.github.cdgeass.generator.settings.comment

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import io.github.cdgeass.generator.ui.PropertiesTable

/**
 * @author cdgeass
 * @since 2021-01-26
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

    private var commentGenerator = project.getService(CommentGenerator::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            group("Properties:") {
                row {
                    cell(PropertiesTable(PROPERTIES, commentGenerator::properties).withToolbarDecorator())
                        .align(Align.FILL)
                        .resizableColumn()
                }
            }
        }
    }
}
