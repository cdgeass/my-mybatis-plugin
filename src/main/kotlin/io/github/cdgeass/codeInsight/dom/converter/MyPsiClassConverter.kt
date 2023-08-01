package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceSet
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Configuration
import io.github.cdgeass.codeInsight.reference.MyJavaClassReference
import io.github.cdgeass.codeInsight.util.findByNamespace
import java.util.*

/**
 * @author cdgeass
 * @since 2021/3/19
 */
class MyPsiClassConverter : Converter<PsiClass>(), CustomReferenceConverter<PsiClass> {

    // mybatis 预定义的限定名
    companion object {
        private val MYBATIS_SHORT_TYPE_NAME_MAP = mapOf(
            Pair("string", "java.lang.String"),

            Pair("byte", "java.lang.Byte"),
            Pair("long", "java.lang.Long"),
            Pair("short", "java.lang.Short"),
            Pair("int", "java.lang.Integer"),
            Pair("integer", "java.lang.Integer"),
            Pair("double", "java.lang.Double"),
            Pair("float", "java.lang.Float"),
            Pair("boolean", "java.lang.Boolean"),

            Pair("byte[]", "java.lang.Byte"),
            Pair("long[]", "java.lang.Long"),
            Pair("short[]", "java.lang.Short"),
            Pair("int[]", "java.lang.Integer"),
            Pair("integer[]", "java.lang.Integer"),
            Pair("double[]", "java.lang.Double"),
            Pair("float[]", "java.lang.Float"),
            Pair("boolean[]", "java.lang.Boolean"),

            Pair("_byte", "java.lang.Byte"),
            Pair("_long", "java.lang.Long"),
            Pair("_short", "java.lang.Short"),
            Pair("_int", "java.lang.Integer"),
            Pair("_integer", "java.lang.Integer"),
            Pair("_double", "java.lang.Double"),
            Pair("_float", "java.lang.Float"),
            Pair("_boolean", "java.lang.Boolean"),

            Pair("_byte[]", "java.lang.Byte"),
            Pair("_long[]", "java.lang.Long"),
            Pair("_short[]", "java.lang.Short"),
            Pair("_int[]", "java.lang.Integer"),
            Pair("_integer[]", "java.lang.Integer"),
            Pair("_double[]", "java.lang.Double"),
            Pair("_float[]", "java.lang.Float"),
            Pair("_boolean[]", "java.lang.Boolean"),

            Pair("date", "java.util.Date"),
            Pair("decimal", "java.math.BigDecimal"),
            Pair("bigdecimal", "java.math.BigDecimal"),
            Pair("biginteger", "java.math.BigInteger"),
            Pair("object", "java.lang.Object"),

            Pair("date[]", "java.util.Date"),
            Pair("decimal[]", "java.math.BigDecimal"),
            Pair("bigdecimal[]", "java.math.BigDecimal"),
            Pair("biginteger[]", "java.math.BigInteger"),
            Pair("object[]", "java.lang.Object"),

            Pair("map", "java.util.Map"),
            Pair("hashmap", "java.util.HashMap"),
            Pair("list", "java.util.List"),
            Pair("arraylist", "java.util.ArrayList"),
            Pair("collection", "java.util.Collection"),
            Pair("iterator", "java.util.Iterator"),

            Pair("resultset", "java.sql.ResultSet"),
        )
    }

    override fun toString(t: PsiClass?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiClass? {
        if (s.isNullOrBlank() || context?.referenceXmlElement == null) {
            return null
        }

        return resolvePsiClass(context.referenceXmlElement!!)
    }

    private fun resolvePsiClass(element: PsiElement): PsiClass? {
        if (element !is XmlAttributeValue) return null

        val project = element.project
        var name = element.value

        // 限定名
        name = MYBATIS_SHORT_TYPE_NAME_MAP[name.lowercase(Locale.getDefault())] ?: name
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

    override fun createReferences(
        value: GenericDomValue<PsiClass>?,
        element: PsiElement,
        context: ConvertContext?
    ): Array<JavaClassReference> {
        val str = value?.stringValue ?: return emptyArray()
        val psiClass = value.value ?: return emptyArray()

        return getReferences(psiClass, str, element)
    }

    private fun getReferences(psiClass: PsiClass, str: String, element: PsiElement): Array<JavaClassReference> {
        return object :
            JavaClassReferenceSet(str, element, 0, false, JavaClassReferenceProvider().apply { isSoft = true }) {
            override fun createReference(
                refIndex: Int,
                subRefText: String,
                textRange: TextRange,
                staticImport: Boolean
            ): JavaClassReference {
                return MyJavaClassReference(this, textRange, refIndex, subRefText, staticImport, psiClass)
            }
        }.allReferences
    }
}
