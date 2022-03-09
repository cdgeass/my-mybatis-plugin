package io.github.cdgeass.generator.settings.sqlMap

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
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
                        .horizontalAlign(HorizontalAlign.FILL)
                        .verticalAlign(VerticalAlign.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }
    }
}
