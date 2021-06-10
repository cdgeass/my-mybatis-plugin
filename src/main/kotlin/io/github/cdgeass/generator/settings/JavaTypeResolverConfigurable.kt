package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import io.github.cdgeass.generator.ui.PropertiesTable

/**
 * @author cdgeass
 * @since 2021-01-26
 */
class JavaTypeResolverConfigurable(project: Project) : BoundConfigurable("JavaTypeResolver") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("forceBigDecimals", Boolean::class.java),
            Pair("useJSR310Types", Boolean::class.java)
        )
    }

    private val javaTypeResolver = project.getService(JavaTypeResolver::class.java)

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
