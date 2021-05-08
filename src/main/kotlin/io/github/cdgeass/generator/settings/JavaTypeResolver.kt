package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import io.github.cdgeass.generator.settings.state.JavaTypeResolverState

/**
 * @author cdgeass
 * @since 2021-02-14
 */
@State(name = "JavaTypeResolver", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class JavaTypeResolver : PersistentStateComponent<JavaTypeResolverState> {

    private var state = JavaTypeResolverState()

    override fun getState() = state

    override fun loadState(state: JavaTypeResolverState) {
        this.state = state
    }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }

    companion object {
        fun getInstance(project: Project): JavaTypeResolver {
            return ServiceManager.getService(project, JavaTypeResolver::class.java)
        }
    }

}