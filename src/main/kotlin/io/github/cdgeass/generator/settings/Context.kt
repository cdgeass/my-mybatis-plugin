package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import io.github.cdgeass.generator.settings.state.ContextState

/**
 * @author cdgeass
 * @since 2021-01-26
 */
@State(name = "Context", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class Context : PersistentStateComponent<ContextState> {

    private var state = ContextState()

    override fun getState() = state

    override fun loadState(state: ContextState) {
        this.state = state
    }

    var defaultModelType: String
        get() = state.defaultModelType!!
        set(value) {
            state.defaultModelType = value
        }

    var targetRuntime: String
        get() = state.targetRuntime!!
        set(value) {
            state.targetRuntime = value
        }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }
}
