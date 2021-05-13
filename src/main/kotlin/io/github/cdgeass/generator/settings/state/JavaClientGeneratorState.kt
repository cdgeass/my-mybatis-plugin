package io.github.cdgeass.generator.settings.state

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-18
 */
class JavaClientGeneratorState : BaseState() {

    var type by string("XMLMAPPER")

    var propertiesMap by map<String, String>()

    var properties: MutableMap<String, String>
        get() {
            if (propertiesMap.isEmpty()) {
                return mutableMapOf()
            }
            return propertiesMap
        }
        set(value) {
            this.propertiesMap = value
        }
}
