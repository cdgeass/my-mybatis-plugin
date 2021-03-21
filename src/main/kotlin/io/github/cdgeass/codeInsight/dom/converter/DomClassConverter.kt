package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Configuration
import io.github.cdgeass.codeInsight.dom.reference.JavaDomReference
import io.github.cdgeass.codeInsight.util.findByNamespace

/**
 * @author cdgeass
 * @since 2021/3/19
 */
class DomClassConverter : Converter<PsiClass>(), CustomReferenceConverter<PsiClass> {

    override fun toString(t: PsiClass?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiClass? {
        if (s.isNullOrBlank() || context == null) {
            return null
        }

        // 全限定名
        val element = context.invocationElement
        val scope: GlobalSearchScope? = if (element is GenericDomValue<*>) context.searchScope else null
        val psiClass = DomJavaUtil.findClass(s.trim(), context.file, context.module, scope)
        if (psiClass != null) {
            return psiClass
        }

        // configuration 别名
        val domManager = DomManager.getDomManager(context.project)
        val configurations = findByNamespace("mybatis.configuration", context.project)
            .mapNotNull {
                val domElement = domManager.getDomElement(it.rootTag)
                if (domElement != null && domElement is Configuration) {
                    domElement
                } else {
                    null
                }
            }
        configurations
            .map { it.getTypeAliases() }
            .flatMap { it.getTypeAliases() }
            .forEach {
                if (it.getAlias().value == s && it.getType().value != null) {
                    return it.getType().value
                }
            }

        // 注解别名
        configurations.map { it.getTypeAliases() }
            .flatMap { it.getPackages() }
            .mapNotNull { it.getName().value }
            .flatMap { it.getClasses(GlobalSearchScope.projectScope(context.project)).toList() }
            .forEach {
                val annotation = it.getAnnotation("org.apache.ibatis.type.Alias")
                if (annotation?.findAttributeValue("value")?.text == "\"$s\"") {
                    return it
                }
            }

        return null
    }

    override fun createReferences(
        value: GenericDomValue<PsiClass>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(JavaDomReference(element, value?.value?.let { listOf(it) }))
    }
}