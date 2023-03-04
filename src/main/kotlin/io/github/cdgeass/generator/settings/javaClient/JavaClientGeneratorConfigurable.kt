package io.github.cdgeass.generator.settings.javaClient

import com.intellij.codeInspection.javaDoc.JavadocUIUtil.bindItem
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
class JavaClientGeneratorConfigurable(project: Project) : BoundConfigurable("JavaClientGenerator") {

    companion object {
        val TYPE = listOf(
            "ANNOTATEDMAPPER", "MIXEDMAPPER", "XMLMAPPER"
        )
        val PROPERTIES = linkedMapOf(
            Pair("enableSubPackages", Boolean::class.java),
            Pair("rootInterface", String::class.java),
            Pair("useLegacyBuilder", Boolean::class.java)
        )
    }

    private var javaClientGenerator = project.getService(JavaClientGenerator::class.java)

    override fun createPanel(): DialogPanel {
        return panel {
            row("Type:") {
                comboBox(TYPE)
                    .bindItem(javaClientGenerator::type)
            }
            group("Properties:") {
                row {
                    cell(PropertiesTable(PROPERTIES, javaClientGenerator::properties).withToolbarDecorator())
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }
    }
}
