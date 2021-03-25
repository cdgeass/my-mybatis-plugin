package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.reference.MyDomElementReference
import io.github.cdgeass.codeInsight.util.getMappers

/**
 * @author cdgeass
 * @since 2021/3/20
 */
class MyDomElementConverter : Converter<DomElement>(), CustomReferenceConverter<DomElement> {

    override fun toString(t: DomElement?, context: ConvertContext?): String? {
        return t?.xmlElementName
    }

    override fun fromString(s: String?, context: ConvertContext?): DomElement? {
        if (s.isNullOrBlank() || context?.referenceXmlElement == null) {
            return null
        }

        return getDomElement(s, context.referenceXmlElement!!)
    }

    override fun createReferences(
        value: GenericDomValue<DomElement>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        val domElements = if (value?.value != null) arrayOf(value.value!!) else emptyArray()
        return arrayOf(MyDomElementReference(element, domElements))
    }

    private fun getDomElement(s: String, psiElement: PsiElement, mapper: Mapper? = null): DomElement? {
        val mapper = mapper ?: DomUtil.findDomElement(psiElement, Mapper::class.java) ?: return null
        val domElement = getTarget(s, mapper, psiElement)

        if (domElement != null || !s.contains(".")) {
            return domElement
        }

        // 如果当前 mapper 没有找到则尝试根据 namespace 查询其他 mapper
        val namespace = s.substringBeforeLast(".")
        return getMappers(namespace, psiElement.project).mapNotNull {
            getDomElement(
                s.substringAfterLast("."),
                psiElement,
                it
            )
        }.firstOrNull()
    }

    private fun getTarget(s: String, mapper: Mapper, psiElement: PsiElement): DomElement? {
        if (psiElement !is XmlAttributeValue) {
            return null
        }

        val xmlAttribute = PsiTreeUtil.findFirstParent(psiElement) { it is XmlAttribute } as XmlAttribute
        return when (xmlAttribute.name) {
            "resultMap" -> {
                mapper.getResultMaps().find { resultMap -> resultMap.getId().value == s }
            }
            "refid" -> {
                mapper.getSqlList().find { sql -> sql.getId().value == s }
            }
            else -> {
                null
            }
        }
    }

}