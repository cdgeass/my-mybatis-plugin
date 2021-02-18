package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * @author cdgeass
 * @since 2021-02-18
 */
@State(name = "JavaClient", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class JavaClient : PersistentStateComponent<JavaClientState> {

    private var state = JavaClientState()

    override fun getState() = state

    override fun loadState(state: JavaClientState) {
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

    companion object {
        fun getInstance(project: Project): JavaClient {
            return ServiceManager.getService(project, JavaClient::class.java)
        }
    }

}