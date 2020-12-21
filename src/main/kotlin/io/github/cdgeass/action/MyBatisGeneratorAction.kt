package io.github.cdgeass.action

import com.google.common.base.CaseFormat
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.view.DatabaseView
import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiPackage
import io.github.cdgeass.settings.MyBatisGeneratorSettings
import org.codehaus.plexus.util.StringUtils
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.config.*
import org.mybatis.generator.internal.DefaultShellCallback

/**
 * @author cdgeass
 * @since  2020-09-24
 */
class MyBatisGeneratorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val packageChooserDialog = PackageChooserDialog("Select Package", e.project)
        packageChooserDialog.show()
        val selectedPackage = packageChooserDialog.selectedPackage ?: return

        val selectedTables = DatabaseView.getSelectedElements(e.dataContext, DbTable::class.java)

        val progressManager = ProgressManager.getInstance()
        progressManager.executeNonCancelableSection {
            generateTable(e.project!!, selectedTables, selectedPackage)
        }
    }

    private fun generateTable(project: Project, selectedTables: Set<DbTable>, selectedPackage: PsiPackage) {
        for (selectedTable in selectedTables) {
            generateTable(project, selectedTable, selectedPackage)
        }
    }

    private fun generateTable(project: Project, selectedTable: DbTable, selectedPackage: PsiPackage) {
        val dataSource = selectedTable.dataSource.delegate as LocalDataSource
        val configuration = Configuration()

        for (classPathElement in dataSource.classpathElements) {
            for (classesRootUrl in classPathElement.classesRootUrls) {
                configuration.addClasspathEntry(StringUtils.replace(classesRootUrl, "file://", ""))
            }
        }

        val settings = MyBatisGeneratorSettings.getInstance()

        val context = Context(ModelType.getModelType(settings.defaultModelType))
        context.id = dataSource.name
        context.targetRuntime = settings.targetRuntime
        settings.contextProperties.forEach { (property, value) -> context.addProperty(property, value) }

        val connectionConfig = dataSource.connectionConfig!!
        val jdbcConnectionConfiguration = JDBCConnectionConfiguration()
        jdbcConnectionConfiguration.connectionURL = connectionConfig.url
        jdbcConnectionConfiguration.driverClass = connectionConfig.driverClass
        jdbcConnectionConfiguration.userId = dataSource.username
        // TODO dialog to set password
        jdbcConnectionConfiguration.password = "root"
        context.jdbcConnectionConfiguration = jdbcConnectionConfiguration

        val javaTypeResolverConfiguration = JavaTypeResolverConfiguration()
        settings.javaTypeResolverProperties.forEach { (property, value) ->
            javaTypeResolverConfiguration.addProperty(
                property,
                value
            )
        }

        val javaModelGeneratorConfiguration = JavaModelGeneratorConfiguration()
        javaModelGeneratorConfiguration.targetProject = project.basePath + settings.sourceDir
        javaModelGeneratorConfiguration.targetPackage = selectedPackage.qualifiedName
        settings.javaModelGeneratorProperties.forEach { (property, value) ->
            javaModelGeneratorConfiguration.addProperty(
                property,
                value
            )
        }
        context.javaModelGeneratorConfiguration = javaModelGeneratorConfiguration

        val sqlMapGeneratorConfiguration = SqlMapGeneratorConfiguration()
        sqlMapGeneratorConfiguration.targetProject = project.basePath + settings.resourceDir
        sqlMapGeneratorConfiguration.targetPackage = selectedPackage.qualifiedName
        settings.sqlMapGeneratorProperties.forEach { (property, value) ->
            sqlMapGeneratorConfiguration.addProperty(
                property,
                value
            )
        }
        context.sqlMapGeneratorConfiguration = sqlMapGeneratorConfiguration

        val javaClientGeneratorConfiguration = JavaClientGeneratorConfiguration()
        javaClientGeneratorConfiguration.targetProject = project.basePath + settings.sourceDir
        javaClientGeneratorConfiguration.targetPackage = selectedPackage.qualifiedName
        javaClientGeneratorConfiguration.configurationType = settings.javaClientType
        settings.javaClientProperties.forEach { (property, value) ->
            javaClientGeneratorConfiguration.addProperty(
                property,
                value
            )
        }
        context.javaClientGeneratorConfiguration = javaClientGeneratorConfiguration

        val tableConfiguration = TableConfiguration(context)
        tableConfiguration.schema = DasUtil.getSchema(selectedTable)
        tableConfiguration.tableName = selectedTable.name
        tableConfiguration.domainObjectName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, selectedTable.name)
        tableConfiguration.isInsertStatementEnabled = settings.enableInsert
        tableConfiguration.isSelectByPrimaryKeyStatementEnabled = settings.enableSelectByPrimaryKey
        tableConfiguration.isUpdateByPrimaryKeyStatementEnabled = settings.enableUpdateByPrimaryKey
        tableConfiguration.isDeleteByPrimaryKeyStatementEnabled = settings.enableDeleteByPrimaryKey
        tableConfiguration.isDeleteByExampleStatementEnabled = settings.enableDeleteByExample
        tableConfiguration.isCountByExampleStatementEnabled = settings.enableCountByExample
        tableConfiguration.isUpdateByExampleStatementEnabled = settings.enableUpdateByExample

        tableConfiguration.isWildcardEscapingEnabled = settings.modelEscapeWildCards
        tableConfiguration.isDelimitIdentifiers = settings.delimitIdentifiers
        tableConfiguration.isAllColumnDelimitingEnabled = settings.delimitAllColumns
        settings.tableProperties.forEach { (property, value) -> tableConfiguration.addProperty(property, value) }
        context.addTableConfiguration(tableConfiguration)

        configuration.addContext(context)

        val warnings = mutableListOf<String>()
        val myBatisGenerator = MyBatisGenerator(configuration, DefaultShellCallback(true), warnings)
        myBatisGenerator.generate(null)
    }
}