package io.github.cdgeass.generator.action

import com.google.common.base.CaseFormat
import com.intellij.database.access.DatabaseCredentials
import com.intellij.database.dataSource.DatabaseCredentialsAuthProvider
import com.intellij.database.dataSource.DatabaseCredentialsAuthProviderUi
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.view.getSelectedDbElements
import com.intellij.ide.util.PackageChooserDialog
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SelectFromListDialog
import com.intellij.psi.PsiPackage
import com.intellij.util.containers.JBIterable
import io.github.cdgeass.PluginBundle
import io.github.cdgeass.generator.settings.comment.CommentGenerator
import io.github.cdgeass.generator.settings.javaClient.JavaClientGenerator
import io.github.cdgeass.generator.settings.javaModel.JavaModelGenerator
import io.github.cdgeass.generator.settings.javaType.JavaTypeResolver
import io.github.cdgeass.generator.settings.settings.Settings
import io.github.cdgeass.generator.settings.sqlMap.SqlMapGenerator
import io.github.cdgeass.generator.settings.table.Table
import org.codehaus.plexus.util.StringUtils
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.config.*
import org.mybatis.generator.internal.DefaultShellCallback
import java.sql.SQLException
import javax.swing.ListSelectionModel

/**
 * @author cdgeass
 * @since 2020-09-24
 */
class MyBatisGeneratorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedTables = e.dataContext.getSelectedDbElements(DbTable::class.java)
        if (selectedTables.isEmpty) {
            return
        }
        generateTables(e.project!!, selectedTables, 0)
    }

    private fun generateTables(project: Project, selectedTables: JBIterable<DbTable>, depth: Int) {
        if (!computeModuleAndPackage(project, selectedTables)) {
            return
        }

        val dataSourceMap = selectedTables.groupBy { DbImplUtil.getLocalDataSource(it.dataSource) }
        val configuration = Configuration()
        dataSourceMap.forEach { (dataSource, dbTables) ->
            for (classPathElement in dataSource.classpathElements) {
                for (classesRootUrl in classPathElement.classesRootUrls) {
                    configuration.addClasspathEntry(StringUtils.replace(classesRootUrl, "file://", ""))
                }
            }

            val schemaMap = dbTables.groupBy { DasUtil.getSchema(it) }
            schemaMap.forEach { (schema, schemaDbTables) ->
                val context = buildContext(project, dataSource)
                    .apply {
                        jdbcConnectionConfiguration = buildJdbcConnectionConfiguration(dataSource)
                        javaTypeResolverConfiguration = buildJavaTypeResolverConfiguration(project)
                        commentGeneratorConfiguration = buildCommentGeneratorConfiguration(project)
                        buildPlugins(project).forEach { addPluginConfiguration(it) }

                        javaModelGeneratorConfiguration = buildJavaModelGeneratorConfiguration(project, schema)
                        sqlMapGeneratorConfiguration = buildSqlMapGeneratorConfiguration(project, schema)
                        javaClientGeneratorConfiguration = buildJavaClientGeneratorConfiguration(project, schema)

                        schemaDbTables.forEach { schemaDbTable ->
                            addTableConfiguration(
                                buildTableConfiguration(
                                    project,
                                    this,
                                    schemaDbTable
                                )
                            )
                        }
                    }

                configuration.addContext(context)
            }
        }

        val warnings = mutableListOf<String>()
        val myBatisGenerator = MyBatisGenerator(configuration, DefaultShellCallback(true), warnings)

        try {
            myBatisGenerator.generate(GenerateProgressCallback(warnings))
        } catch (e: Exception) {
            if (e is SQLException && depth == 0) {
                // 连接异常 请求密码
                dataSourceMap.forEach { (dataSource, _) ->
                    DatabaseCredentialsAuthProviderUi.askCredentials(
                        e.localizedMessage, true,
                        dataSource.mutableConfig,
                        project,
                        DatabaseCredentials.getInstance()
                    )
                }
                generateTables(project, selectedTables, 1)
            } else {
                NotificationGroupManager.getInstance().getNotificationGroup("Generator Error")
                    .createNotification(e.message ?: "", NotificationType.ERROR)
                    .notify(project)
            }
        }
    }

    /**
     * 检查是否选择表对应Schema已经进行过生成
     * 若未进行过则要求选择对应的Module已经Package
     */
    private fun computeModuleAndPackage(
        project: Project,
        selectedTables: JBIterable<DbTable>
    ): Boolean {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules

        val settings = project.getService(Settings::class.java)
        val schemaPackages = settings.schemaPackages

        for (selectedTable in selectedTables) {
            val schema = DasUtil.getSchema(selectedTable)
            val modelAndClientPackage = schemaPackages[schema]

            val modelPackage = modelAndClientPackage?.keys?.first() ?: selectModuleAndPackage(
                modules,
                PluginBundle.message("generator.module.selector.model.title")
            )

            val clientPackage = modelAndClientPackage?.values?.first() ?: selectModuleAndPackage(
                modules,
                PluginBundle.message("generator.module.selector.client.title")
            )

            if (modelPackage == null || clientPackage == null) {
                return false
            }

            schemaPackages[schema] = mapOf(Pair(modelPackage, clientPackage))
        }
        settings.schemaPackages = schemaPackages

        return true
    }

    private fun selectModuleAndPackage(modules: Array<Module>, title: String): String? {
        val module = selectModule(modules, title)
        if (module != null) {
            val psiPackage = selectPackage(module, title)
            if (psiPackage != null) {
                return module.name + ":" + psiPackage.qualifiedName
            }
        }
        return null
    }

    private fun selectModule(modules: Array<Module>, title: String): Module? {
        if (modules.size == 1) {
            return modules[0]
        } else {
            val selectedModules = SelectFromListDialog(
                null,
                modules,
                { (it as Module).name },
                title,
                ListSelectionModel.SINGLE_SELECTION
            ).apply { show() }.selection

            if (selectedModules.isEmpty()) {
                Messages.showErrorDialog(
                    PluginBundle.message("generator.module.selector.noModule"),
                    PluginBundle.message("generator.title")
                )
            } else {
                return selectedModules[0] as Module
            }
        }
        return null
    }

    private fun selectPackage(module: Module, title: String): PsiPackage? {
        return PackageChooserDialog(title, module)
            .apply {
                show()
            }
            .selectedPackage
    }

    private fun getModulePathAndPackage(moduleAndPackage: String, project: Project): Pair<String, String> {
        val split = moduleAndPackage.split(":")

        val moduleName = split[0]
        val moduleManager = ModuleManager.getInstance(project)
        val module = moduleManager.findModuleByName(moduleName)!!

        val moduleRootManager = ModuleRootManager.getInstance(module)
        val contentRoot = moduleRootManager.contentRootUrls[0].substringAfter("file://")
        return Pair(contentRoot, split[1])
    }

    private fun buildContext(project: Project, dataSource: LocalDataSource): Context {
        val context = project.getService(io.github.cdgeass.generator.settings.context.Context::class.java)
        return Context(ModelType.getModelType(context.defaultModelType))
            .apply {
                id = dataSource.name
                targetRuntime = context.targetRuntime
                context.properties.forEach { (property, value) -> addProperty(property, value) }
            }
    }

    private fun buildJdbcConnectionConfiguration(
        dataSource: LocalDataSource
    ): JDBCConnectionConfiguration {
        val password = DatabaseCredentialsAuthProvider.getCredentials(dataSource)?.getPasswordAsString()
        return JDBCConnectionConfiguration()
            .apply {
                connectionURL = dataSource.url
                driverClass = dataSource.driverClass
                userId = dataSource.username
                this.password = password
            }
    }

    private fun buildJavaTypeResolverConfiguration(project: Project): JavaTypeResolverConfiguration {
        val javaTypeResolver = project.getService(JavaTypeResolver::class.java)
        return JavaTypeResolverConfiguration()
            .apply {
                javaTypeResolver.properties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildJavaModelGeneratorConfiguration(
        project: Project,
        schema: String
    ): JavaModelGeneratorConfiguration {
        val settings = project.getService(Settings::class.java)
        val moduleAndPackage = getModulePathAndPackage(settings.schemaPackages[schema]!!.keys.first(), project)

        val javaModelGenerator = project.getService(JavaModelGenerator::class.java)
        return JavaModelGeneratorConfiguration()
            .apply {
                targetProject = moduleAndPackage.first + settings.sourceDir
                targetPackage = moduleAndPackage.second
                javaModelGenerator.properties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildSqlMapGeneratorConfiguration(
        project: Project,
        schema: String
    ): SqlMapGeneratorConfiguration {
        val settings = project.getService(Settings::class.java)
        val moduleAndPackage = getModulePathAndPackage(settings.schemaPackages[schema]!!.values.first(), project)

        val sqlMapGenerator = project.getService(SqlMapGenerator::class.java)
        return SqlMapGeneratorConfiguration()
            .apply {
                targetProject = moduleAndPackage.first + settings.resourcesDir
                targetPackage = moduleAndPackage.second
                sqlMapGenerator.properties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildJavaClientGeneratorConfiguration(
        project: Project,
        schema: String
    ): JavaClientGeneratorConfiguration {
        val settings = project.getService(Settings::class.java)
        val moduleAndPackage = getModulePathAndPackage(settings.schemaPackages[schema]!!.values.first(), project)

        val javaClientGenerator = project.getService(JavaClientGenerator::class.java)
        return JavaClientGeneratorConfiguration()
            .apply {
                targetProject = moduleAndPackage.first + settings.sourceDir
                targetPackage = moduleAndPackage.second
                configurationType = javaClientGenerator.type
                javaClientGenerator.properties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildTableConfiguration(
        project: Project,
        context: Context,
        selectedTable: DbTable
    ): TableConfiguration {
        val settings = project.getService(Settings::class.java)
        val table = project.getService(Table::class.java)
        return TableConfiguration(context)
            .apply {
                schema = DasUtil.getSchema(selectedTable)
                tableName = selectedTable.name

                val finalTableName = tableName
                    .replace(Regex("^${settings.modelNamePrefixPattern}"), "")
                    .replace(Regex("${settings.modelNameSuffixPattern}$"), "")

                domainObjectName = settings.modelNameFormat.let { it.ifBlank { "%s" } }
                    .format(
                        CaseFormat.LOWER_UNDERSCORE.to(
                            CaseFormat.UPPER_CAMEL,
                            finalTableName
                        )
                    )
                mapperName = settings.clientNameFormat.let { it.ifBlank { "%sMapper" } }
                    .format(
                        CaseFormat.LOWER_UNDERSCORE.to(
                            CaseFormat.UPPER_CAMEL,
                            finalTableName
                        )
                    )
                // insert
                isInsertStatementEnabled = table.enableInsert
                // select
                isSelectByPrimaryKeyStatementEnabled = table.enableSelectByPrimaryKey
                isSelectByExampleStatementEnabled = table.enableSelectByExample
                // update
                isUpdateByPrimaryKeyStatementEnabled = table.enableUpdateByPrimaryKey
                isUpdateByExampleStatementEnabled = table.enableUpdateByExample
                // delete
                isDeleteByPrimaryKeyStatementEnabled = table.enableDeleteByPrimaryKey
                isDeleteByExampleStatementEnabled = table.enableDeleteByExample
                // count
                isCountByExampleStatementEnabled = table.enableCountByExample
                table.properties.forEach { (property, value) -> addProperty(property, value) }
            }
    }

    private fun buildCommentGeneratorConfiguration(project: Project): CommentGeneratorConfiguration {
        val commentGenerator = project.getService(CommentGenerator::class.java)
        return CommentGeneratorConfiguration()
            .apply {
                commentGenerator.properties.forEach { (property, value) ->
                    addProperty(
                        property, value
                    )
                }
            }
    }

    private fun buildPlugins(project: Project): List<PluginConfiguration> {
        val settings = project.getService(Settings::class.java)
        val commentGenerator = project.getService(CommentGenerator::class.java)

        val plugins = mutableListOf<PluginConfiguration>()
        if (settings.enableLombok) {
            plugins.add(
                PluginConfiguration().apply {
                    configurationType = "io.github.cdgeass.generator.plugin.LombokDataAnnotationPlugin"
                }
            )
        }
        if (settings.enableGeneric) {
            plugins.add(
                PluginConfiguration().apply {
                    configurationType = "io.github.cdgeass.generator.plugin.RootInterfaceGenericPlugin"
                }
            )
        }
        if (commentGenerator.suppressAllComments()) {
            plugins.add(
                PluginConfiguration().apply {
                    configurationType = "org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin"
                }
            )
        }
        return plugins
    }
}
