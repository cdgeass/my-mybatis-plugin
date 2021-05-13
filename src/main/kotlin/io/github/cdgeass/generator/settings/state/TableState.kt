package io.github.cdgeass.generator.settings.state

import com.intellij.openapi.components.BaseState

/**
 * @author cdgeass
 * @since 2021-02-18
 */
class TableState : BaseState() {

    var enableInsert by property(true)

    var enableSelectByPrimaryKey by property(true)

    var enableSelectByExample by property(false)

    var enableUpdateByPrimaryKey by property(true)

    var enableDeleteByPrimaryKey by property(true)

    var enableDeleteByExample by property(false)

    var enableCountByExample by property(false)

    var enableUpdateByExample by property(false)

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
