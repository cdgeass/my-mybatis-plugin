package io.github.cdgeass.generator.settings.context

import com.intellij.codeInspection.javaDoc.JavadocUIUtil.bindItem
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
class ContextConfigurable(project: Project) : BoundConfigurable("Context") {

    companion object {
        val DEFAULT_MODEL_TYPE = listOf("conditional", "flat", "hierarchical")
        val TARGET_RUNTIME = listOf(
            "MyBatis3DynamicSql", "MyBatis3Kotlin", "MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSqlV1"
        )
        val PROPERTIES = linkedMapOf(
            Pair("autoDelimitKeywords", Boolean::class.java),
            Pair("beginningDelimiter", String::class.java),
            Pair("endingDelimiter", String::class.java),
            Pair("javaFileEncoding", String::class.java),
            Pair("targetJava8", Boolean::class.java),
            Pair("kotlinFileEncoding", String::class.java)
        )
    }

    private val context = project.getService(Context::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            row("DefaultModelType:") {
                comboBox(DEFAULT_MODEL_TYPE)
                    .bindItem(context::defaultModelType)
            }
            row("TargetRuntime:") {
                comboBox(TARGET_RUNTIME)
                    .bindItem(context::targetRuntime)
            }
            group("Properties:") {
                row {
                    cell(PropertiesTable(PROPERTIES, context::properties).withToolbarDecorator())
                        .horizontalAlign(HorizontalAlign.FILL)
                        .verticalAlign(VerticalAlign.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }
    }
}
