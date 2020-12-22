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

    private lateinit var settingsComponent: MyBatisGeneratorSettingsComponent

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "MyBatis Generator"
    }

    override fun createComponent(): JComponent {
        settingsComponent = MyBatisGeneratorSettingsComponent()
        return settingsComponent.getComponent()
    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
        val settings = MyBatisGeneratorSettings.getInstance()

        val context = settingsComponent.getContext()
        settings.defaultModelType = context.getDefaultModelType()
        settings.targetRuntime = context.getTargetRuntime()
        settings.contextProperties = context.getProperties()

        val javaTypeResolver = context.getJavaTypeResolver()
        settings.javaTypeResolverProperties = javaTypeResolver.getProperties()

        val javaModelGenerator = context.getJavaModelGenerator()
        settings.javaModelGeneratorProperties = javaModelGenerator.getProperties()

        val sqlMapGenerator = context.getSqlMapGenerator()
        settings.sqlMapGeneratorProperties = sqlMapGenerator.getProperties()

        val javaClient = context.getJavaClientGenerator()
        settings.javaClientType = javaClient.getType()
        settings.javaClientProperties = javaClient.getProperties()

        val table = context.getTable()
        settings.enableInsert = table.isEnableInsert()
        settings.enableSelectByPrimaryKey = table.isEnableSelectByPrimaryKey()
        settings.enableSelectByExample = table.isEnableSelectByExample()
        settings.enableUpdateByPrimaryKey = table.isEnableUpdateByPrimaryKey()
        settings.enableDeleteByPrimaryKey = table.isEnableDeleteByPrimaryKey()
        settings.enableDeleteByExample = table.isEnableDeleteByExample()
        settings.enableCountByExample = table.isEnableCountByExample()
        settings.enableUpdateByExample = table.isEnableUpdateByExample()
        settings.selectByPrimaryKeyQueryId = table.isSelectByPrimaryKeyQueryId()
        settings.selectByExampleQueryId = table.isSelectByExampleQueryId()
        settings.modelType = table.getModelType()
        settings.modelEscapeWildCards = table.isModelEscapeWildcards()
        settings.delimitIdentifiers = table.isDelimitIdentifiers()
        settings.delimitAllColumns = table.isDelimitAllColumns()
        settings.tableProperties = table.getProperties()

        val commentGenerator = context.getCommentGenerator()
        settings.commentGeneratorProperties = commentGenerator.getProperties()
    }

    override fun reset() {
        val settings = MyBatisGeneratorSettings.getInstance()

        val context = settingsComponent.getContext()
        context.setDefaultModelType(settings.defaultModelType)
        context.setTargetRuntime(settings.targetRuntime)
        context.setProperties(settings.contextProperties)

        val javaTypeResolver = context.getJavaTypeResolver()
        javaTypeResolver.setProperties(settings.javaTypeResolverProperties)

        val javaModelGenerator = context.getJavaModelGenerator()
        javaModelGenerator.setProperties(settings.javaModelGeneratorProperties)

        val sqlMapGenerator = context.getSqlMapGenerator()
        sqlMapGenerator.setProperties(settings.sqlMapGeneratorProperties)

        val javaClientGenerator = context.getJavaClientGenerator()
        javaClientGenerator.setType(settings.javaClientType)
        javaClientGenerator.setProperties(settings.javaClientProperties)

        val table = context.getTable()
        table.setEnableInsert(settings.enableInsert)
        table.setEnableSelectByPrimaryKey(settings.enableSelectByPrimaryKey)
        table.setEnableSelectByExample(settings.enableSelectByExample)
        table.setEnableUpdateByPrimaryKey(settings.enableUpdateByPrimaryKey)
        table.setDeleteByPrimaryKey(settings.enableDeleteByPrimaryKey)
        table.setDeleteByExample(settings.enableDeleteByExample)
        table.setCountByExample(settings.enableCountByExample)
        table.setUpdateByExample(settings.enableUpdateByExample)
        table.setModelType(settings.modelType)
        table.setModelEscapeWildCards(settings.modelEscapeWildCards)
        table.setDelimitIdentifiers(settings.delimitIdentifiers)
        table.setDelimitAllColumns(settings.delimitAllColumns)
        table.setProperties(settings.tableProperties)

        val commentGenerator = context.getCommentGenerator()
        commentGenerator.setProperties(settings.commentGeneratorProperties)
    }

}