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
class SqlMapGeneratorConfigurable(project: Project) : BoundConfigurable("SqlMapGenerator") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("enableSubPackages", Boolean::class.java)
        )
    }

    private var sqlMapGenerator = project.getService(SqlMapGenerator::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, sqlMapGenerator::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }
}
