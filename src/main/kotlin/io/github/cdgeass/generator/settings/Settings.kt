package io.github.cdgeass.generator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * @author cdgeass
 * @since  2020-09-25
 */
@State(
    name = "io.github.cdgeass.settings.MyBatisGeneratorSettings",
    storages = [Storage("MyBatisGeneratorSettings.xml")]
)
class Settings(
    // Context
    var defaultModelType: String = "conditional",
    var targetRuntime: String = "MyBatis3Simple",
    var contextProperties: Map<String, String> = emptyMap(),

    // JavaTypeResolver
    var javaTypeResolverProperties: Map<String, String> = emptyMap(),

    // JavaModelGenerator
    var javaModelGeneratorProperties: Map<String, String> = emptyMap(),

    // SqlMapGenerator
    var sqlMapGeneratorProperties: Map<String, String> = emptyMap(),

    // JavaClientGenerator
    var javaClientType: String = "",
    var javaClientProperties: Map<String, String> = emptyMap(),

    // Table
    var enableInsert: Boolean = true,
    var enableSelectByPrimaryKey: Boolean = true,
    var enableSelectByExample: Boolean = false,
    var enableUpdateByPrimaryKey: Boolean = true,
    var enableDeleteByPrimaryKey: Boolean = false,
    var enableDeleteByExample: Boolean = false,
    var enableCountByExample: Boolean = false,
    var enableUpdateByExample: Boolean = false,
    var selectByPrimaryKeyQueryId: Boolean = false,
    var selectByExampleQueryId: Boolean = false,
    var modelType: String = "conditional",
    var modelEscapeWildCards: Boolean = false,
    var delimitIdentifiers: Boolean = false,
    var delimitAllColumns: Boolean = false,
    var tableProperties: Map<String, String> = emptyMap(),

    // comment generator
    var commentGeneratorProperties: Map<String, String> = emptyMap(),
) : PersistentStateComponent<Settings> {

    companion object Factory {
        fun getInstance(project: Project): Settings {
            return ServiceManager.getService(project, Settings::class.java)
        }
    }

    override fun getState(): Settings {
        return this
    }

    override fun loadState(state: Settings) {
        XmlSerializerUtil.copyBean(state, this)
    }

}