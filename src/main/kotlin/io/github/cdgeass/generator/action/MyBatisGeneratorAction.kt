package io.github.cdgeass.generator.action

import com.google.common.base.CaseFormat
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.view.DatabaseView
import com.intellij.icons.AllIcons
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SelectFromListDialog
import io.github.cdgeass.generator.settings.MyBatisGeneratorSettings
import org.codehaus.plexus.util.StringUtils
import org.intellij.lang.annotations.JdkConstants
import org.jdesktop.swingx.calendar.DateSelectionModel
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.config.*
import org.mybatis.generator.internal.DefaultShellCallback
import javax.swing.ListSelectionModel

/**
 * @author cdgeass
 * @since  2020-09-24
 */
class MyBatisGeneratorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedTables = DatabaseView.getSelectedElements(e.dataContext, DbTable::class.java)
        generateTable(e.project!!, selectedTables)
    }

    private fun generateTable(project: Project, selectedTables: Set<DbTable>) {
        val settings = MyBatisGeneratorSettings.getInstance()
        computeModuleAndPackage(project, settings, selectedTables)

        for (selectedTable in selectedTables) {
            generateTable(project, settings, selectedTable)
        }
    }

    /**
     * 检查是否选择表对应Schema已经进行过生成
     * 若未进行过则要求选择对应的Module已经Package
     */
    private fun computeModuleAndPackage(
        project: Project,
        settings: MyBatisGeneratorSettings,
        selectedTables: Set<DbTable>
    ) {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules

        for (selectedTable in selectedTables) {
            val schema = DasUtil.getSchema(selectedTable)
            val module: Module
            if (settings.schemaModule[schema] == null) {
                if (modules.size == 1) {
                    module = modules[0]
                } else {
                    val selectFromListDialog = SelectFromListDialog(
                        project,
                        modules,
                        { (it as Module).name },
                        "Select Module To Generate",
                        ListSelectionModel.SINGLE_SELECTION
                    )
                    selectFromListDialog.show()
                    val selectedModules = selectFromListDialog.selection
                    if (selectedModules.isEmpty()) {
                        Messages.showErrorDialog("Please select a module to generate", "MyBatis Generator")
                        throw RuntimeException("Please select a module to generate")
                    }
                    module = (selectedModules[0] as Module)
                }
                settings.schemaModule[schema] = module.name
            } else {
                module = moduleManager.findModuleByName(settings.schemaModule[schema]!!)!!
            }

            settings.schemaModelPackages.computeIfAbsent(schema) {
                PackageChooserDialog("Select A Package To Generate Model", module)
                    .apply { show() }
                    .selectedPackage
                    .qualifiedName
            }

            settings.schemaClientPackages.computeIfAbsent(schema) {
                PackageChooserDialog("Select A Package To Generate Client", module)
                    .apply { show() }
                    .selectedPackage
                    .qualifiedName
            }
        }
    }

    private fun generateTable(
        project: Project,
        settings: MyBatisGeneratorSettings,
        selectedTable: DbTable
    ) {
        val schema = DasUtil.getSchema(selectedTable)
        val dataSource = DbImplUtil.getLocalDataSource(selectedTable.dataSource)

        val configuration = Configuration()
        for (classPathElement in dataSource.classpathElements) {
            for (classesRootUrl in classPathElement.classesRootUrls) {
                configuration.addClasspathEntry(StringUtils.replace(classesRootUrl, "file://", ""))
            }
        }

        val context = buildContext(settings, dataSource)
            .apply {
                jdbcConnectionConfiguration = buildJdbcConnectionConfiguration(dataSource)
                javaTypeResolverConfiguration = buildJavaTypeResolverConfiguration(settings)
                javaModelGeneratorConfiguration =
                    buildJavaModelGeneratorConfiguration(settings, project, schema)
                sqlMapGeneratorConfiguration =
                    buildSqlMapGeneratorConfiguration(settings, project, schema)
                javaClientGeneratorConfiguration =
                    buildJavaClientGeneratorConfiguration(settings, project, schema)
                commentGeneratorConfiguration = buildCommentGeneratorConfiguration(settings)
                addTableConfiguration(buildTableConfiguration(settings, this, selectedTable))
                buildPlugins().forEach { addPluginConfiguration(it) }
            }

        configuration.addContext(context)

        val warnings = mutableListOf<String>()
        val myBatisGenerator = MyBatisGenerator(configuration, DefaultShellCallback(true), warnings)
        myBatisGenerator.generate(null)
    }

    private fun buildContext(settings: MyBatisGeneratorSettings, dataSource: LocalDataSource): Context {
        return Context(ModelType.getModelType(settings.defaultModelType))
            .apply {
                id = dataSource.name
                targetRuntime = settings.targetRuntime
                settings.contextProperties.forEach { (property, value) -> addProperty(property, value) }
            }
    }

    private fun buildJdbcConnectionConfiguration(dataSource: LocalDataSource): JDBCConnectionConfiguration {
        val password: String
        val credentialAttributes = CredentialAttributes(generateServiceName("my-mybatis", dataSource.url!!))
        val credentials = PasswordSafe.instance.get(credentialAttributes)
        if (credentials?.getPasswordAsString() != null) {
            password = credentials.getPasswordAsString()!!
        } else {
            password = Messages.showInputDialog(
                "Input the password of %s".format(dataSource.name),
                "MyBatis Generator", AllIcons.Actions.Commit
            )!!
            PasswordSafe.instance.setPassword(credentialAttributes, password)
        }

        return JDBCConnectionConfiguration()
            .apply {
                connectionURL = dataSource.url
                driverClass = dataSource.driverClass
                userId = dataSource.username
                this.password = password
            }
    }

    private fun buildJavaTypeResolverConfiguration(settings: MyBatisGeneratorSettings): JavaTypeResolverConfiguration {
        return JavaTypeResolverConfiguration()
            .apply {
                settings.javaTypeResolverProperties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildJavaModelGeneratorConfiguration(
        settings: MyBatisGeneratorSettings,
        project: Project,
        schema: String
    ): JavaModelGeneratorConfiguration {
        val module = ModuleManager.getInstance(project).findModuleByName(settings.schemaModule[schema]!!)!!
        return JavaModelGeneratorConfiguration()
            .apply {
                targetProject = ModuleUtil.getModuleDirPath(module) + settings.sourceDir
                targetPackage = settings.schemaModelPackages[schema]
                    settings.javaModelGeneratorProperties.forEach { (property, value) ->
                        addProperty(
                            property,
                            value
                        )
                    }
            }
    }

    private fun buildSqlMapGeneratorConfiguration(
        settings: MyBatisGeneratorSettings,
        project: Project,
        schema: String
    ): SqlMapGeneratorConfiguration {
        val module = ModuleManager.getInstance(project).findModuleByName(settings.schemaModule[schema]!!)!!
        return SqlMapGeneratorConfiguration()
            .apply {
                targetProject = ModuleUtil.getModuleDirPath(module) + settings.resourceDir
                targetPackage = settings.schemaClientPackages[schema]
                settings.sqlMapGeneratorProperties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildJavaClientGeneratorConfiguration(
        settings: MyBatisGeneratorSettings,
        project: Project,
        schema: String
    ): JavaClientGeneratorConfiguration {
        val module = ModuleManager.getInstance(project).findModuleByName(settings.schemaModule[schema]!!)!!
        return JavaClientGeneratorConfiguration()
            .apply {
                targetProject = ModuleUtil.getModuleDirPath(module) + settings.sourceDir
                targetPackage = settings.schemaClientPackages[schema]
                configurationType = settings.javaClientType
                settings.javaClientProperties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildTableConfiguration(
        settings: MyBatisGeneratorSettings,
        context: Context,
        selectedTable: DbTable
    ): TableConfiguration {
        return TableConfiguration(context)
            .apply {
                schema = DasUtil.getSchema(selectedTable)
                tableName = selectedTable.name
                domainObjectName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, selectedTable.name)
                isInsertStatementEnabled = settings.enableInsert
                isSelectByPrimaryKeyStatementEnabled = settings.enableSelectByPrimaryKey
                isUpdateByPrimaryKeyStatementEnabled = settings.enableUpdateByPrimaryKey
                isDeleteByPrimaryKeyStatementEnabled = settings.enableDeleteByPrimaryKey
                isDeleteByExampleStatementEnabled = settings.enableDeleteByExample
                isCountByExampleStatementEnabled = settings.enableCountByExample
                isUpdateByExampleStatementEnabled = settings.enableUpdateByExample

                isWildcardEscapingEnabled = settings.modelEscapeWildCards
                isDelimitIdentifiers = settings.delimitIdentifiers
                isAllColumnDelimitingEnabled = settings.delimitAllColumns
                settings.tableProperties.forEach { (property, value) -> addProperty(property, value) }
            }
    }

    private fun buildCommentGeneratorConfiguration(settings: MyBatisGeneratorSettings): CommentGeneratorConfiguration {
        return CommentGeneratorConfiguration()
            .apply {
                settings.commentGeneratorProperties.forEach { (property, value) ->
                    addProperty(
                        property, value
                    )
                }
            }
    }

    private fun buildPlugins(): List<PluginConfiguration> {
        return listOf(
            PluginConfiguration().apply {
                configurationType = "io.github.cdgeass.generator.plugin.LombokDataAnnotationPlugin"
            },
            PluginConfiguration().apply {
                configurationType = "io.github.cdgeass.generator.plugin.RootInterfaceGenericPlugin"
            }
        )
    }
}