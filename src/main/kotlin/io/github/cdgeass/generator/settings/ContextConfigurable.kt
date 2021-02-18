package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import io.github.cdgeass.generator.ui.PropertiesTable
import javax.swing.DefaultComboBoxModel

/**
 * @author cdgeass
 * @since  2021-01-26
 */
class ContextConfigurable(project: Project) : BoundConfigurable("Context") {

    companion object {
        val DEFAULT_MODEL_TYPE = arrayOf("conditional", "flat", "hierarchical")
        val TARGET_RUNTIME = arrayOf(
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

    private val context = Context.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row("DefaultModelType:") {
                comboBox(
                    DefaultComboBoxModel(DEFAULT_MODEL_TYPE),
                    context::defaultModelType
                )
            }
            row("TargetRuntime:") {
                comboBox(
                    DefaultComboBoxModel(TARGET_RUNTIME),
                    context::targetRuntime
                )
            }
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, context::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }
}
