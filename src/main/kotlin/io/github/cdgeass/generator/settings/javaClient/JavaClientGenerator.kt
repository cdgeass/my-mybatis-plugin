package io.github.cdgeass.generator.settings.javaClient

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * @author cdgeass
 * @since 2021-02-18
 */
@State(name = "JavaClientGenerator", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class JavaClientGenerator : PersistentStateComponent<JavaClientGeneratorState> {

    private var state = JavaClientGeneratorState()

    override fun getState() = state

    override fun loadState(state: JavaClientGeneratorState) {
        this.state = state
    }

    var type: String
        get() = state.type!!
        set(value) {
            state.type = value
        }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }
}
