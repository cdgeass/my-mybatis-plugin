package io.github.cdgeass.generator.settings.javaModel

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * @author cdgeass
 * @since 2021-02-18
 */
@State(name = "JavaModelGenerator", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class JavaModelGenerator : PersistentStateComponent<JavaModelGeneratorState> {

    private var state = JavaModelGeneratorState()

    override fun getState() = state

    override fun loadState(state: JavaModelGeneratorState) {
        this.state = state
    }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }
}
