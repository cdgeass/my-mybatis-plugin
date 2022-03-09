package io.github.cdgeass.generator.settings.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

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

    var modelNamePrefixPattern: String
        get() = state.modelNamePrefixPattern ?: ""
        set(value) {
            state.modelNamePrefixPattern = value
        }

    var modelNameSuffixPattern: String
        get() = state.modelNameSuffixPattern ?: ""
        set(value) {
            state.modelNameSuffixPattern = value
        }

    var modelNameFormat: String
        get() = state.modelNameFormat ?: ""
        set(value) {
            state.modelNameFormat = value
        }

    var clientNameFormat: String
        get() = state.clientNameFormat ?: ""
        set(value) {
            state.clientNameFormat = value
        }

    var enableLombok: Boolean
        get() = state.enableLombok
        set(value) {
            state.enableLombok = value
        }

    var enableGeneric: Boolean
        get() = state.enableGeneric
        set(value) {
            state.enableGeneric = value
        }
}
