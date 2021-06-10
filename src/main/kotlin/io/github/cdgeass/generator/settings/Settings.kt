package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import io.github.cdgeass.generator.settings.state.SettingsState

/**
 * @author cdgeass
 * @since 2020-09-25
 */
@State(name = "Settings", storages = [Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator")])
class Settings : PersistentStateComponent<SettingsState> {

    private var state = SettingsState()

    override fun getState() = state

    override fun loadState(state: SettingsState) {
        this.state = state
    }

    var sourceDir: String
        get() = state.sourceDir!!
        set(value) {
            state.sourceDir = value
        }

    var resourcesDir: String
        get() = state.resourcesDir!!
        set(value) {
            state.resourcesDir = value
        }

    var schemaPackages: MutableMap<String, Map<String, String>>
        get() = state.schemaPackages
        set(value) {
            state.schemaPackages = value
        }

    var modelNamePattern: String
        get() = state.modelNamePattern ?: ""
        set(value) {
            state.modelNamePattern = value
        }

    var clientNamePattern: String
        get() = state.clientNamePattern ?: ""
        set(value) {
            state.clientNamePattern = value
        }

    var enableLombok: Boolean
        get() = state.enableLombok
        set(value) {
            state.enableLombok = value
        }

    companion object Factory {
        fun getInstance(project: Project): Settings {
            return ServiceManager.getService(project, Settings::class.java)
        }
    }
}
