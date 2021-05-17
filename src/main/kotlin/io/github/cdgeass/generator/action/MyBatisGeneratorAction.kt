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
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SelectFromListDialog
import com.intellij.psi.PsiPackage
import io.github.cdgeass.PluginBundle
import io.github.cdgeass.generator.settings.CommentGenerator
import io.github.cdgeass.generator.settings.JavaClientGenerator
import io.github.cdgeass.generator.settings.JavaModelGenerator
import io.github.cdgeass.generator.settings.JavaTypeResolver
import io.github.cdgeass.generator.settings.Settings
import io.github.cdgeass.generator.settings.SqlMapGenerator
import io.github.cdgeass.generator.settings.Table
import org.codehaus.plexus.util.StringUtils
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.config.CommentGeneratorConfiguration
import org.mybatis.generator.config.Configuration
import org.mybatis.generator.config.Context
import org.mybatis.generator.config.JDBCConnectionConfiguration
import org.mybatis.generator.config.JavaClientGeneratorConfiguration
import org.mybatis.generator.config.JavaModelGeneratorConfiguration
import org.mybatis.generator.config.JavaTypeResolverConfiguration
import org.mybatis.generator.config.ModelType
import org.mybatis.generator.config.PluginConfiguration
import org.mybatis.generator.config.SqlMapGeneratorConfiguration
import org.mybatis.generator.config.TableConfiguration
import org.mybatis.generator.internal.DefaultShellCallback
import javax.swing.ListSelectionModel

/**
 * @author cdgeass
 * @since 2020-09-24
 */
class MyBatisGeneratorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedTables = DatabaseView.getSelectedElements(e.dataContext, DbTable::class.java)
        if (selectedTables.isEmpty()) {
            return
        }

        generateTables(e.project!!, selectedTables)
    }

    private fun generateTables(project: Project, selectedTables: Set<DbTable>) {
        computeModuleAndPackage(project, selectedTables)

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
                        jdbcConnectionConfiguration = buildJdbcConnectionConfiguration(project, dataSource)
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

        myBatisGenerator.generate(GenerateProgressCallback())
    }

    /**
     * 检查是否选择表对应Schema已经进行过生成
     * 若未进行过则要求选择对应的Module已经Package
     */
    private fun computeModuleAndPackage(
        project: Project,
        selectedTables: Set<DbTable>
    ) {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules

        val settings = Settings.getInstance(project)
        val schemaPackages = settings.schemaPackages

        for (selectedTable in selectedTables) {
            val schema = DasUtil.getSchema(selectedTable)
            val modelAndClientPackage = schemaPackages[schema]

            val modelPackage = modelAndClientPackage?.keys?.first() ?: selectModuleAndPackage(
                modules,
                PluginBundle.message("generator.module.selector.client.title")
            )

            val clientPackage = modelAndClientPackage?.values?.first() ?: selectModuleAndPackage(
                modules,
                PluginBundle.message("generator.module.selector.model.title")
            )

            schemaPackages[schema] = mapOf(Pair(modelPackage, clientPackage))
        }
        settings.schemaPackages = schemaPackages
    }

    private fun selectModuleAndPackage(modules: Array<Module>, title: String): String {
        val module = selectModule(modules, title)
        if (module != null) {
            val psiPackage = selectPackage(module, title)
            if (psiPackage != null) {
                return module.name + ":" + psiPackage.qualifiedName
            }
        }
        throw RuntimeException()
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
        return PackageChooserDialog(title, module).apply { show() }.selectedPackage
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
        val context = io.github.cdgeass.generator.settings.Context.getInstance(project)
        return Context(ModelType.getModelType(context.defaultModelType))
            .apply {
                id = dataSource.name
                targetRuntime = context.targetRuntime
                context.properties.forEach { (property, value) -> addProperty(property, value) }
            }
    }

    private fun buildJdbcConnectionConfiguration(
        project: Project,
        dataSource: LocalDataSource
    ): JDBCConnectionConfiguration {
        val password: String
        val credentialAttributes = CredentialAttributes(generateServiceName("my-mybatis", dataSource.url!!))
        val credentials = PasswordSafe.instance.get(credentialAttributes)
        if (credentials?.getPasswordAsString() != null) {
            password = credentials.getPasswordAsString()!!
        } else {
            password = Messages.showPasswordDialog(
                project, PluginBundle.message("generator.datasource.password.input.title", dataSource.name),
                PluginBundle.message("generator.title"), AllIcons.Actions.Commit
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

    private fun buildJavaTypeResolverConfiguration(project: Project): JavaTypeResolverConfiguration {
        val javaTypeResolver = JavaTypeResolver.getInstance(project)
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
        val settings = Settings.getInstance(project)
        val moduleAndPackage = getModulePathAndPackage(settings.schemaPackages[schema]!!.keys.first(), project)

        val javaModelGenerator = JavaModelGenerator.getInstance(project)
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
        val settings = Settings.getInstance(project)
        val moduleAndPackage = getModulePathAndPackage(settings.schemaPackages[schema]!!.values.first(), project)

        val sqlMapGenerator = SqlMapGenerator.getInstance(project)
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
        val settings = Settings.getInstance(project)
        val moduleAndPackage = getModulePathAndPackage(settings.schemaPackages[schema]!!.values.first(), project)

        val javaClientGenerator = JavaClientGenerator.getInstance(project)
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
        val table = Table.getInstance(project)
        return TableConfiguration(context)
            .apply {
                schema = DasUtil.getSchema(selectedTable)
                tableName = selectedTable.name
                domainObjectName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, selectedTable.name)
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
        val commentGenerator = CommentGenerator.getInstance(project)
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
        val settings = Settings.getInstance(project)
        val commentGenerator = CommentGenerator.getInstance(project)

        val plugins = mutableListOf<PluginConfiguration>()
        if (settings.enableLombok) {
            plugins.add(
                PluginConfiguration().apply {
                    configurationType = "io.github.cdgeass.generator.plugin.LombokDataAnnotationPlugin"
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
