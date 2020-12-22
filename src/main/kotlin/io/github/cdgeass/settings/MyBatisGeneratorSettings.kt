package io.github.cdgeass.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * @author cdgeass
 * @since  2020-09-25
 */
@State(
        name = "io.github.cdgeass.settings.MyBatisGeneratorSettings",
        storages = [Storage("MyBatisGeneratorSettings.xml")]
)
class MyBatisGeneratorSettings(
    // Context
    var defaultModelType: String = "",
    var targetRuntime: String = "",
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
    var enableSelectByExample: Boolean = true,
    var enableUpdateByPrimaryKey: Boolean = true,
    var enableDeleteByPrimaryKey: Boolean = true,
    var enableDeleteByExample: Boolean = true,
    var enableCountByExample: Boolean = true,
    var enableUpdateByExample: Boolean = true,
    var selectByPrimaryKeyQueryId: Boolean = true,
    var selectByExampleQueryId: Boolean = true,
    var modelType: String = "",
    var modelEscapeWildCards: Boolean = true,
    var delimitIdentifiers: Boolean = true,
    var delimitAllColumns: Boolean = true,
    var tableProperties: Map<String, String> = emptyMap(),

    // comment generator
    var commentGeneratorProperties: Map<String, String> = emptyMap(),

    var sourceDir: String = "/src/main/java",
    var resourceDir: String = "/src/main/resources"
) : PersistentStateComponent<MyBatisGeneratorSettings> {

    companion object Factory {
        fun getInstance(): MyBatisGeneratorSettings {
            return ServiceManager.getService(MyBatisGeneratorSettings::class.java)
        }
    }

    override fun getState(): MyBatisGeneratorSettings? {
        return this
    }

    override fun loadState(state: MyBatisGeneratorSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

}