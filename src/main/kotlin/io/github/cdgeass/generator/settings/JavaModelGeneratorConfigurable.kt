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
class JavaModelGeneratorConfigurable(project: Project) : BoundConfigurable("JavaModelGenerator") {

    companion object {
        val PROPERTIES = linkedMapOf(
            Pair("constructorBased", Boolean::class.java),
            Pair("enableSubPackages", Boolean::class.java),
            Pair("exampleTargetPackages", String::class.java),
            Pair("exampleTargetProject", String::class.java),
            Pair("immutable", Boolean::class.java),
            Pair("rootClass", String::class.java),
            Pair("trimStrings", Boolean::class.java)
        )
    }

    private var javaModelGenerator = JavaModelGenerator.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, javaModelGenerator::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }

}