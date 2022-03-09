package io.github.cdgeass.generator.settings.comment

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-18
 */
class CommentGeneratorState : BaseState() {

    var propertiesMap by map<String, String>()

    var properties: MutableMap<String, String>
        get() {
            if (propertiesMap.isEmpty()) {
                return mutableMapOf(
                    Pair("suppressDate", "true"),
                    Pair("addRemarkComments", "true")
                )
            }
            return propertiesMap
        }
        set(value) {
            this.propertiesMap = value
        }
}
