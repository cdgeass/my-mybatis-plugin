package io.github.cdgeass.settings

import com.intellij.openapi.options.Configurable
import io.github.cdgeass.component.MyBatisGeneratorSettingsComponent
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * @author cdgeass
 * @since  2020-09-25
 */
class MyBatisGeneratorSettingsConfigurable : Configurable {

    private lateinit var myBatisGeneratorSettingsComponent: MyBatisGeneratorSettingsComponent

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "MyBatis Generator"
    }

    override fun createComponent(): JComponent? {
        myBatisGeneratorSettingsComponent = MyBatisGeneratorSettingsComponent()
        return myBatisGeneratorSettingsComponent.getPanel()
    }

    override fun isModified(): Boolean {
        val myBatisGeneratorSettings = MyBatisGeneratorSettings.getInstance()!!
        var modified = myBatisGeneratorSettings.sourceFolder == myBatisGeneratorSettingsComponent.getSourceFolder()
        modified = modified || myBatisGeneratorSettings.resourceFolder == myBatisGeneratorSettingsComponent.getResourceFolder()
        return modified
    }

    override fun apply() {
        val myBatisGeneratorSettings = MyBatisGeneratorSettings.getInstance()!!
        myBatisGeneratorSettings.sourceFolder = myBatisGeneratorSettingsComponent.getSourceFolder()
        myBatisGeneratorSettings.resourceFolder = myBatisGeneratorSettingsComponent.getResourceFolder()
    }

    override fun reset() {
        val myBatisGeneratorSettings = MyBatisGeneratorSettings.getInstance()!!
        myBatisGeneratorSettingsComponent.setSourceFolder(myBatisGeneratorSettings.sourceFolder)
        myBatisGeneratorSettingsComponent.setResourceFolder(myBatisGeneratorSettings.resourceFolder)
    }

}