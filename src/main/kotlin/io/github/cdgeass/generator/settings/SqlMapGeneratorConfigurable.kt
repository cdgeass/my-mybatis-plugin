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
class SqlMapGeneratorConfigurable(project: Project) : BoundConfigurable("SqlMapGenerator") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("enableSubPackages", Boolean::class.java)
        )
    }

    private var sqlMapGenerator = SqlMapGenerator.getInstance(project)

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