package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import io.github.cdgeass.generator.ui.PropertiesTable

/**
 * @author cdgeass
 * @since  2021-01-26
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

    private var table = Table.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                checkBox("EnableInsert", table::enableInsert)
            }
            row {
                checkBox("EnableSelectByPrimaryKey", table::enableSelectByPrimaryKey)
                checkBox("EnableSelectByExample", table::enableSelectByExample)
            }
            row {
                checkBox("EnableUpdateByPrimaryKey", table::enableUpdateByPrimaryKey)
                checkBox("EnableUpdateByExample", table::enableUpdateByExample)
            }
            row {
                checkBox("EnableUpdateByPrimaryKey", table::enableUpdateByPrimaryKey)
                checkBox("EnableUpdateByExample", table::enableUpdateByExample)
            }
            row {
                checkBox("EnableDeleteByPrimaryKey", table::enableDeleteByPrimaryKey)
                checkBox("EnableDeleteByExample", table::enableDeleteByExample)
            }
            row {
                checkBox("EnableCountByExample", table::enableCountByExample)
            }
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, table::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }
}