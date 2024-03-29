package io.github.cdgeass.generator.settings.javaModel

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

    private var javaModelGenerator = project.getService(JavaModelGenerator::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            group("Properties:") {
                row {
                    cell(PropertiesTable(PROPERTIES, javaModelGenerator::properties).withToolbarDecorator())
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }
    }
}
