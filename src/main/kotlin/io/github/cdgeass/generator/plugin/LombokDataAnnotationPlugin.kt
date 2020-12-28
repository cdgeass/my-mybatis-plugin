package io.github.cdgeass.generator.plugin

import org.mybatis.generator.api.IntrospectedColumn
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.Plugin
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.java.TopLevelClass

/**
 * @author cdgeass
 * @since  2020-12-25
 */
class LombokDataAnnotationPlugin : PluginAdapter() {

    override fun validate(warnings: MutableList<String>): Boolean {
        return true
    }

    override fun modelBaseRecordClassGenerated(
        topLevelClass: TopLevelClass,
        introspectedTable: IntrospectedTable
    ): Boolean {
        addLombokDataAnnotation(topLevelClass)

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable)
    }

    override fun modelPrimaryKeyClassGenerated(
        topLevelClass: TopLevelClass,
        introspectedTable: IntrospectedTable
    ): Boolean {
        addLombokDataAnnotation(topLevelClass)

        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable)
    }

    override fun modelRecordWithBLOBsClassGenerated(
        topLevelClass: TopLevelClass,
        introspectedTable: IntrospectedTable
    ): Boolean {
        addLombokDataAnnotation(topLevelClass)

        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable)
    }

    private fun addLombokDataAnnotation(topLevelClass: TopLevelClass) {
        topLevelClass.addImportedType("lombok.Data")
        topLevelClass.addAnnotation("@Data")
    }

    override fun modelGetterMethodGenerated(
        method: Method,
        topLevelClass: TopLevelClass,
        introspectedColumn: IntrospectedColumn,
        introspectedTable: IntrospectedTable,
        modelClassType: Plugin.ModelClassType
    ): Boolean {
        return false
    }

    override fun modelSetterMethodGenerated(
        method: Method,
        topLevelClass: TopLevelClass,
        introspectedColumn: IntrospectedColumn,
        introspectedTable: IntrospectedTable,
        modelClassType: Plugin.ModelClassType
    ): Boolean {
        return false
    }
}