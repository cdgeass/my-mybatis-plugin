package io.github.cdgeass.generator.settings.state

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-28
 */
class SettingsState : BaseState() {

    var enableLombok by property(true)

    var sourceDir by string("/src/main/java")

    var resourcesDir by string("/src/main/resources")

}