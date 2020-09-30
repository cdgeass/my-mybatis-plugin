package io.github.cdgeass.action

import com.google.common.base.CaseFormat
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.view.DatabaseView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
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
        val selectedTables = DatabaseView.getSelectedElements(e.dataContext, DbTable::class.java)

        val progressManager = ProgressManager.getInstance()
        progressManager.executeNonCancelableSection {
            configure(e.project!!, selectedTables)
        }
    }

    private fun configure(project: Project, selectedTables: Set<DbTable>) {
        val configuration = Configuration()
        for (selectedTable in selectedTables) {
            configure(project, selectedTable, configuration)
        }
    }

    private fun configure(project: Project, selectedTable: DbTable, configuration: Configuration) {
        val dataSource = selectedTable.dataSource.delegate as LocalDataSource

        for (classPathElement in dataSource.classpathElements) {
            for (classesRootUrl in classPathElement.classesRootUrls) {
                configuration.addClasspathEntry(StringUtils.replace(classesRootUrl, "file://", ""))
            }
        }

        val settings = MyBatisGeneratorSettings.getInstance()!!

        val context = Context(ModelType.FLAT)
        context.id = dataSource.name
        context.targetRuntime = settings.targetRuntime

        val connectionConfig = dataSource.connectionConfig!!
        val jdbcConnectionConfiguration = JDBCConnectionConfiguration()
        jdbcConnectionConfiguration.connectionURL = connectionConfig.url
        jdbcConnectionConfiguration.driverClass = connectionConfig.driverClass
        jdbcConnectionConfiguration.userId = dataSource.username
        // TODO dialog to set password
        jdbcConnectionConfiguration.password = "root"
        context.jdbcConnectionConfiguration = jdbcConnectionConfiguration

        val javaModelGeneratorConfiguration = JavaModelGeneratorConfiguration()
        javaModelGeneratorConfiguration.targetProject = project.basePath + settings.sourceDir
        // TODO dialog to set package
        javaModelGeneratorConfiguration.targetPackage = "io.cdgeass.github"
        context.javaModelGeneratorConfiguration = javaModelGeneratorConfiguration

        val sqlMapGeneratorConfiguration = SqlMapGeneratorConfiguration()
        sqlMapGeneratorConfiguration.targetProject = project.basePath + settings.resourceDir
        // TODO dialog to set package
        sqlMapGeneratorConfiguration.targetPackage = "io.cdgeass.github"
        context.sqlMapGeneratorConfiguration = sqlMapGeneratorConfiguration

        val javaClientGeneratorConfiguration = JavaClientGeneratorConfiguration()
        javaClientGeneratorConfiguration.targetProject = project.basePath + settings.sourceDir
        // TODO dialog to set package
        javaClientGeneratorConfiguration.targetPackage = "io.cdgeass.github"
        javaClientGeneratorConfiguration.configurationType = settings.configurationType
        context.javaClientGeneratorConfiguration = javaClientGeneratorConfiguration

        val tableConfiguration = TableConfiguration(context)
        tableConfiguration.schema = DasUtil.getSchema(selectedTable)
        tableConfiguration.tableName = selectedTable.name
        tableConfiguration.domainObjectName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, selectedTable.name)
        context.addTableConfiguration(tableConfiguration)

        configuration.addContext(context)

        val warnings = mutableListOf<String>()
        val myBatisGenerator = MyBatisGenerator(configuration, DefaultShellCallback(true), warnings)
        myBatisGenerator.generate(null)

        println(warnings)
    }
}