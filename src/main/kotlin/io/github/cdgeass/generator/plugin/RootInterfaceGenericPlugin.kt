package io.github.cdgeass.generator.plugin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType
import org.mybatis.generator.api.dom.java.Interface
import org.mybatis.generator.api.dom.java.TopLevelClass

/**
 * @author cdgeass
 * @since 2020-12-28
 */
class RootInterfaceGenericPlugin : PluginAdapter() {

    override fun validate(warnings: MutableList<String>): Boolean {
        return true
    }

    private var domainType: String? = null

    override fun modelBaseRecordClassGenerated(
        topLevelClass: TopLevelClass?,
        introspectedTable: IntrospectedTable?
    ): Boolean {
        domainType = topLevelClass?.type?.fullyQualifiedName

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable)
    }

    override fun clientGenerated(interfaze: Interface, introspectedTable: IntrospectedTable): Boolean {
        val superInterface =
            interfaze.superInterfaceTypes.firstOrNull()
                ?: return super.clientGenerated(interfaze, introspectedTable)
        if (superInterface.typeArguments.contains(FullyQualifiedJavaType("T"))) {
            superInterface.typeArguments.clear()
            if (domainType != null) {
                superInterface.typeArguments.add(FullyQualifiedJavaType(domainType))
            }
        }

        return super.clientGenerated(interfaze, introspectedTable)
    }
}
