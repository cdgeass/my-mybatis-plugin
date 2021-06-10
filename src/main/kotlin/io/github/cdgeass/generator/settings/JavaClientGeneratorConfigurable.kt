package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import io.github.cdgeass.generator.ui.PropertiesTable
import javax.swing.DefaultComboBoxModel

/**
 * @author cdgeass
 * @since 2021-01-26
 */
class JavaClientGeneratorConfigurable(project: Project) : BoundConfigurable("JavaClientGenerator") {

    companion object {
        val TYPE = arrayOf(
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
                comboBox(
                    DefaultComboBoxModel(TYPE),
                    javaClientGenerator::type
                )
            }
            row {
                panel(
                    "Properties:",
                    PropertiesTable(PROPERTIES, javaClientGenerator::properties).withToolbarDecorator(),
                    false
                )
            }
        }
    }
}
