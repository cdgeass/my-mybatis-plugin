package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel

/**
 * @author cdgeass
 * @since  2020-09-25
 */
class SettingsConfigurable(project: Project) : BoundConfigurable("MyBatis Generator") {

    private val settings = Settings.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                checkBox("EnableLombok", settings::enableLombok)
            }
        }
    }

}