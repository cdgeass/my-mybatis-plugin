package io.github.cdgeass.generator.settings.table

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import io.github.cdgeass.generator.ui.PropertiesTable

/**
 * @author cdgeass
 * @since 2021-01-26
 */
class TableConfigurable(project: Project) : BoundConfigurable("Table") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("constructorBased", Boolean::class.java),
            Pair("ignoreQualifiersAtRuntime", Boolean::class.java),
            Pair("immutable", Boolean::class.java),
            Pair("modelOnly", Boolean::class.java),
            Pair("rootClass", String::class.java),
            Pair("rootInterface", String::class.java),
            Pair("runtimeCatalog", String::class.java),
            Pair("runtimeSchema", String::class.java),
            Pair("runtimeTableName", String::class.java),
            Pair("selectAllOrderByClause", String::class.java),
            Pair("trimStrings", Boolean::class.java),
            Pair("useActualColumnNames", Boolean::class.java),
            Pair("useColumnIndexes", Boolean::class.java),
            Pair("useCompoundPropertyNames", Boolean::class.java)
        )
    }

    private var table = project.getService(Table::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                checkBox("EnableInsert").bindSelected(table::enableInsert)
            }
            twoColumnsRow({
                checkBox("EnableSelectByPrimaryKey").bindSelected(table::enableSelectByPrimaryKey)
            }, {
                checkBox("EnableSelectByExample").bindSelected(table::enableSelectByExample)
            })
            twoColumnsRow({
                checkBox("EnableUpdateByPrimaryKey").bindSelected(table::enableUpdateByPrimaryKey)
            }, {
                checkBox("EnableUpdateByExample").bindSelected(table::enableUpdateByExample)
            })
            twoColumnsRow({
                checkBox("EnableDeleteByPrimaryKey").bindSelected(table::enableDeleteByPrimaryKey)
            }, {
                checkBox("EnableDeleteByExample").bindSelected(table::enableDeleteByExample)
            })
            row {
                checkBox("EnableCountByExample").bindSelected(table::enableCountByExample)
            }
            group("Properties:") {
                row {
                    cell(PropertiesTable(PROPERTIES, table::properties).withToolbarDecorator())
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }
    }
}
