package io.github.cdgeass.generator.plugin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType
import org.mybatis.generator.api.dom.java.Interface

/**
 * @author cdgeass
 * @since 2020-12-28
 */
class RootInterfaceGenericPlugin : PluginAdapter() {

    override fun validate(warnings: MutableList<String>): Boolean {
        return true
    }

    override fun clientGenerated(interfaze: Interface, introspectedTable: IntrospectedTable): Boolean {
        val superInterface =
            interfaze.superInterfaceTypes.firstOrNull()
                ?: return super.clientGenerated(interfaze, introspectedTable)
        if (superInterface.typeArguments.contains(FullyQualifiedJavaType("T"))) {
            superInterface.typeArguments.clear()
        }
        val schema = introspectedTable.tableConfiguration.schema
        val domainObjectName = introspectedTable.tableConfiguration.domainObjectName
//        val selectedModelPackage = MyBatisGeneratorSettings.getInstance().schemaModelPackages[schema]
//        if (selectedModelPackage != null) {
//            superInterface.typeArguments.add(FullyQualifiedJavaType("$selectedModelPackage.$domainObjectName"))
//        }

        return super.clientGenerated(interfaze, introspectedTable)
    }
}
