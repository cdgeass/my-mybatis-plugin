package io.github.cdgeass.generator.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel

/**
 * @author cdgeass
 * @since  2020-09-25
 */
class SettingsConfigurable(private val project: Project) : BoundConfigurable("MyBatis Generator") {

    override fun createPanel(): DialogPanel {
        TODO("Not yet implemented")
    }

}