package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import io.github.cdgeass.generator.settings.state.TableState

/**
 * @author cdgeass
 * @since 2021-02-18
 */
@State(name = "Table", storages = [(Storage("io.github.cdgeass.my-mybatis.MyBatisGenerator"))])
class Table : PersistentStateComponent<TableState> {

    private var state = TableState()

    override fun getState() = state

    override fun loadState(state: TableState) {
        this.state = state
    }

    var properties: MutableMap<String, String>
        get() = state.properties
        set(value) {
            state.properties = value
        }

    var enableInsert: Boolean
        get() = state.enableInsert
        set(value) {
            state.enableInsert = value
        }

    var enableSelectByPrimaryKey: Boolean
        get() = state.enableSelectByPrimaryKey
        set(value) {
            state.enableSelectByPrimaryKey = value
        }

    var enableSelectByExample: Boolean
        get() = state.enableSelectByExample
        set(value) {
            state.enableSelectByExample = value
        }

    var enableUpdateByPrimaryKey: Boolean
        get() = state.enableUpdateByPrimaryKey
        set(value) {
            state.enableUpdateByPrimaryKey = value
        }

    var enableDeleteByPrimaryKey: Boolean
        get() = state.enableDeleteByPrimaryKey
        set(value) {
            state.enableDeleteByPrimaryKey = value
        }

    var enableDeleteByExample: Boolean
        get() = state.enableDeleteByExample
        set(value) {
            state.enableDeleteByPrimaryKey = value
        }

    var enableCountByExample: Boolean
        get() = state.enableCountByExample
        set(value) {
            state.enableCountByExample = value
        }

    var enableUpdateByExample: Boolean
        get() = state.enableUpdateByExample
        set(value) {
            state.enableUpdateByExample = value
        }

    companion object {
        fun getInstance(project: Project): Table {
            return ServiceManager.getService(project, Table::class.java)
        }
    }

}