package io.github.cdgeass.generator.settings.state

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-28
 */
class SettingsState : BaseState() {

    var sourceDir by string("/src/main/java")

    var resourcesDir by string("/src/main/resources")

    var schemaPackages by map<String, Map<String, String>>()

    var modelNamePrefixPattern by string("")

    var modelNameSuffixPattern by string("")

    var modelNameFormat by string("")

    var clientNameFormat by string("")

    var enableLombok by property(true)

    var enableGeneric by property(true)
}
