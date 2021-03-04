package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * @author cdgeass
 * @since  2020-09-25
 */
@State(name = "Settings", storages = [Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator")])
class Settings : PersistentStateComponent<SettingsState> {

    private var state = SettingsState()

    override fun getState() = state

    override fun loadState(state: SettingsState) {
        this.state = state
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