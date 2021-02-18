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
class JavaTypeResolverConfigurable(project: Project) : BoundConfigurable("JavaTypeResolver") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("forceBigDecimals", Boolean::class.java),
            Pair("useJSR310Types", Boolean::class.java)
        )
    }

    private val javaTypeResolver = JavaTypeResolver.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, javaTypeResolver::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }

}