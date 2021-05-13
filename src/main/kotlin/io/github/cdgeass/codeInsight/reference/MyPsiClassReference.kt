package io.github.cdgeass.codeInsight.reference

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.DomManager
import io.github.cdgeass.codeInsight.dom.element.Configuration
import io.github.cdgeass.codeInsight.util.findByNamespace

/**
 * @author cdgeass
 * @since 2021/3/26
 */
// mybatis 预定义的限定名
private val MYBATIS_SHORT_TYPE_NAME_MAP = mapOf(
    Pair("boolean", "java.lang.Boolean"),
    Pair("byte", "java.lang.Byte"),
    Pair("short", "java.lang.Short"),
    Pair("int", "java.lang.Integer"),
    Pair("integer", "java.lang.Integer"),
    Pair("long", "java.lang.Long"),
    Pair("float", "java.lang.Float"),
    Pair("double", "java.lang.Double")
)

class MyPsiClassReference(
    element: PsiElement,
) : PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement? {
        return resolvePsiClass(myElement)
    }
}

fun resolvePsiClass(element: PsiElement): PsiClass? {
    if (element !is XmlAttributeValue) return null

    val project = element.project

    var name = element.value

    // 限定名
    name = MYBATIS_SHORT_TYPE_NAME_MAP[name.toLowerCase()] ?: name
    val psiClass = JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.allScope(project))
    if (psiClass != null) {
        return psiClass
    }

    // configuration 别名
    val domManager = DomManager.getDomManager(project)
    val configurations = findByNamespace("mybatis.configuration", project)
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
            if (it.getAlias().value == name && it.getType().value != null) {
                return it.getType().value
            }
        }

    // 注解别名
    configurations.map { it.getTypeAliases() }
        .flatMap { it.getPackages() }
        .mapNotNull { it.getName().value }
        .flatMap { it.getClasses(GlobalSearchScope.projectScope(project)).toList() }
        .forEach {
            val annotation = it.getAnnotation("org.apache.ibatis.type.Alias")
            if (annotation?.findAttributeValue("value")?.text == "\"$name\"") {
                return it
            }
        }

    return null
}
