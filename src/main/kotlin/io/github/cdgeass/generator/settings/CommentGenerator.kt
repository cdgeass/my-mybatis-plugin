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
@State(name = "CommentGenerator", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class CommentGenerator : PersistentStateComponent<CommentGeneratorState> {

    private var state = CommentGeneratorState()

    override fun getState() = state

    override fun loadState(state: CommentGeneratorState) {
        this.state = state
    }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }

    companion object {
        fun getInstance(project: Project): CommentGenerator {
            return ServiceManager.getService(project, CommentGenerator::class.java)
        }
    }

}