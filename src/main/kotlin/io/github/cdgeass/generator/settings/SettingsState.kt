package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-28
 */
class SettingsState : BaseState() {

    var enableLombok by property(true)

}