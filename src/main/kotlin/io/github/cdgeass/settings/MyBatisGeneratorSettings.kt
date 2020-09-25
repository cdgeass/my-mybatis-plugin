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
        var sourceFolder: String = "/",
        var resourceFolder: String = "/"
) : PersistentStateComponent<MyBatisGeneratorSettings> {

    companion object Factory {
        fun getInstance(): MyBatisGeneratorSettings? {
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