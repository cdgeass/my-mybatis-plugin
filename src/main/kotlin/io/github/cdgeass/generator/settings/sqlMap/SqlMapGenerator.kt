package io.github.cdgeass.generator.settings.sqlMap

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * @author cdgeass
 * @since 2021-02-18
 */
@State(name = "SqlMapGenerator", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class SqlMapGenerator : PersistentStateComponent<SqlMapGeneratorState> {

    private var state = SqlMapGeneratorState()

    override fun getState() = state

    override fun loadState(state: SqlMapGeneratorState) {
        this.state = state
    }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }
}
