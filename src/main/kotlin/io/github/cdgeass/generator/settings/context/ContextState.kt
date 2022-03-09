package io.github.cdgeass.generator.settings.context

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-01-26
 */
class ContextState : BaseState() {

    var defaultModelType by string("conditional")

    var targetRuntime by string("MyBatis3")

    var propertiesMap by map<String, String>()

    var properties: MutableMap<String, String>
        get() {
            if (propertiesMap.isEmpty()) {
                return mutableMapOf(
                    Pair("javaFileEncoding", "UTF-8")
                )
            }
            return propertiesMap
        }
        set(value) {
            this.propertiesMap = value
        }
}
