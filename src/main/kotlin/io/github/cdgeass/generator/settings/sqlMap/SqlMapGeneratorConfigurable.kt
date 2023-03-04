package io.github.cdgeass.generator.settings.sqlMap

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
class SqlMapGeneratorConfigurable(project: Project) : BoundConfigurable("SqlMapGenerator") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("enableSubPackages", Boolean::class.java)
        )
    }

    private var sqlMapGenerator = project.getService(SqlMapGenerator::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            group("Properties:") {
                row {
                    cell(PropertiesTable(PROPERTIES, sqlMapGenerator::properties).withToolbarDecorator())
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }
    }
}
