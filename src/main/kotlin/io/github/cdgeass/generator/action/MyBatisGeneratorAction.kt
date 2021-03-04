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
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SelectFromListDialog
import io.github.cdgeass.generator.settings.*
import org.apache.commons.lang3.tuple.MutablePair
import org.codehaus.plexus.util.StringUtils
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.config.*
import org.mybatis.generator.config.Context
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
        computeModuleAndPackage(project, selectedTables)

        for (selectedTable in selectedTables) {
            generateTable(project, selectedTable)
        }
    }

    companion object {
        private const val CLIENT_MODULE_PREFIX = "MYBATIS_GENERATOR_CLIENT_MODULE_"
        private const val CLIENT_PACKAGE_PREFIX = "MYBATIS_GENERATOR_CLIENT_PACKAGE_"
        private const val MODEL_MODULE_PREFIX = "MYBATIS_GENERATOR_MODEL_MODULE_"
        private const val MODEL_PACKAGE_PREFIX = "MYBATIS_GENERATOR_MODEL_PACKAGE_"

        private const val SOURCE_DIR = "MYBATIS_GENERATOR_SOURCE_DIR"
        private const val SOURCE_DIR_DEFAULT_VALUE = "/src/main/java"
        private const val RESOURCES_DIR = "MYBATIS_GENERATOR_RESOURCES_DIR"
        private const val RESOURCES_DIR_DEFAULT_VALUE = "/src/main/resources"
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

        val computeModule = { modulePair: MutablePair<String?, Module?>,
                              target: String ->
            if (modulePair.left == null) {
                if (modules.size == 1) {
                    modulePair.right = modules[0]
                } else {
                    val selectFromListDialog = SelectFromListDialog(
                        project,
                        modules,
                        { (it as Module).name },
                        "Select Module To Generate $target",
                        ListSelectionModel.SINGLE_SELECTION
                    )
                    selectFromListDialog.show()
                    val selectedModules = selectFromListDialog.selection
                    if (selectedModules.isEmpty()) {
                        Messages.showErrorDialog("Please select a module to generate", "MyBatis Generator")
                        throw RuntimeException("Please Select A Module To Generate $target")
                    }
                    modulePair.right = (selectedModules[0] as Module)
                }
                modulePair.left = modulePair.right!!.name
            } else {
                modulePair.right = moduleManager.findModuleByName(modulePair.left!!)!!
            }
        }

        val propertiesComponent = PropertiesComponent.getInstance(project)
        for (selectedTable in selectedTables) {
            val schema = DasUtil.getSchema(selectedTable)

            // 选择client的生成路径
            val clientModulePair: MutablePair<String?, Module?> =
                MutablePair(propertiesComponent.getValue(CLIENT_MODULE_PREFIX + schema), null)
            computeModule(clientModulePair, "Client")
            if (propertiesComponent.getValue(CLIENT_PACKAGE_PREFIX + schema)?.isNotBlank() != true) {
                val selectedPackage =
                    PackageChooserDialog("Select A Package To Generate Client", clientModulePair.right!!)
                        .apply { show() }
                        .selectedPackage
                        .qualifiedName
                if (selectedPackage.isBlank()) {
                    Messages.showErrorDialog("Please select a package to generate client", "MyBatis Generator")
                    throw RuntimeException("Please Select A Module To Generate Client")
                }
                propertiesComponent.setValue(CLIENT_PACKAGE_PREFIX + schema, selectedPackage)
            }
            propertiesComponent.setValue(CLIENT_MODULE_PREFIX + schema, clientModulePair.left!!)

            // 选择model的生成路径
            val modelModulePair: MutablePair<String?, Module?> =
                MutablePair(propertiesComponent.getValue(MODEL_MODULE_PREFIX + schema), null)
            computeModule(modelModulePair, "Model")
            if (propertiesComponent.getValue(MODEL_PACKAGE_PREFIX + schema)?.isNotBlank() != true) {
                val selectedPackage =
                    PackageChooserDialog("Select A Package To Generate Model", modelModulePair.right!!)
                        .apply { show() }
                        .selectedPackage
                        .qualifiedName
                if (selectedPackage.isBlank()) {
                    Messages.showErrorDialog("Please select a package to generate", "MyBatis Generator")
                    throw RuntimeException("Please Select A Module To Generate Model")
                }
                propertiesComponent.setValue(MODEL_PACKAGE_PREFIX + schema, selectedPackage)
            }
            propertiesComponent.setValue(MODEL_MODULE_PREFIX + schema, modelModulePair.left!!)
        }
    }

    private fun generateTable(
        project: Project,
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

        val context = buildContext(project, dataSource)
            .apply {
                jdbcConnectionConfiguration = buildJdbcConnectionConfiguration(dataSource)
                javaTypeResolverConfiguration = buildJavaTypeResolverConfiguration(project)
                javaModelGeneratorConfiguration = buildJavaModelGeneratorConfiguration(project, schema)
                sqlMapGeneratorConfiguration = buildSqlMapGeneratorConfiguration(project, schema)
                javaClientGeneratorConfiguration = buildJavaClientGeneratorConfiguration(project, schema)
                commentGeneratorConfiguration = buildCommentGeneratorConfiguration(project)

                addTableConfiguration(buildTableConfiguration(project, this, selectedTable))
                buildPlugins(project).forEach { addPluginConfiguration(it) }
            }

        configuration.addContext(context)

        val warnings = mutableListOf<String>()
        val myBatisGenerator = MyBatisGenerator(configuration, DefaultShellCallback(true), warnings)

        myBatisGenerator.generate(GenerateProgressCallback())
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
        val propertiesComponent = PropertiesComponent.getInstance(project)
        val module = ModuleManager.getInstance(project).findModuleByName(
            propertiesComponent.getValue(
                MODEL_MODULE_PREFIX + schema
            )!!
        )!!

        val javaModelGenerator = JavaModelGenerator.getInstance(project)
        return JavaModelGeneratorConfiguration()
            .apply {
                targetProject = ModuleUtil.getModuleDirPath(module) + propertiesComponent.getValue(
                    SOURCE_DIR,
                    SOURCE_DIR_DEFAULT_VALUE
                )
                targetPackage = propertiesComponent.getValue(MODEL_PACKAGE_PREFIX + schema)!!
                javaModelGenerator.properties.forEach { (property, value) ->
                    addProperty(
                        property,
                        value
                    )
                }
            }
    }

    private fun buildSqlMapGeneratorConfiguration(project: Project, schema: String): SqlMapGeneratorConfiguration {
        val propertiesComponent = PropertiesComponent.getInstance(project)
        val module = ModuleManager.getInstance(project).findModuleByName(
            propertiesComponent.getValue(
                CLIENT_MODULE_PREFIX + schema
            )!!
        )!!

        val sqlMapGenerator = SqlMapGenerator.getInstance(project)
        return SqlMapGeneratorConfiguration()
            .apply {
                targetProject = ModuleUtil.getModuleDirPath(module) + propertiesComponent.getValue(
                    RESOURCES_DIR,
                    RESOURCES_DIR_DEFAULT_VALUE
                )
                targetPackage = propertiesComponent.getValue(CLIENT_PACKAGE_PREFIX + schema)
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
        val propertiesComponent = PropertiesComponent.getInstance(project)
        val module = ModuleManager.getInstance(project).findModuleByName(
            propertiesComponent.getValue(
                CLIENT_MODULE_PREFIX + schema
            )!!
        )!!

        val javaClientGenerator = JavaClientGenerator.getInstance(project)
        return JavaClientGeneratorConfiguration()
            .apply {
                targetProject = ModuleUtil.getModuleDirPath(module) + propertiesComponent.getValue(
                    SOURCE_DIR,
                    SOURCE_DIR_DEFAULT_VALUE
                )
                targetPackage = propertiesComponent.getValue(CLIENT_PACKAGE_PREFIX + schema)
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

        val plugins = mutableListOf<PluginConfiguration>()
        if (settings.enableLombok) {
            plugins.add(
                PluginConfiguration().apply {
                    configurationType = "io.github.cdgeass.generator.plugin.LombokDataAnnotationPlugin"
                }
            )
        }
        return plugins
    }
}