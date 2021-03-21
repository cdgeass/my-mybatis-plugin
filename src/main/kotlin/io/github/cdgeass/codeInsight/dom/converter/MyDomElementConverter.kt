package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.reference.MyDomElementReference

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

    private fun getXmlAttributeName(psiElement: PsiElement): String? {
        if (psiElement !is XmlAttributeValue) {
            return null
        }

        val xmlAttribute = PsiTreeUtil.findFirstParent(psiElement) { it is XmlAttribute } as XmlAttribute
        return xmlAttribute.name
    }

    private fun getDomElement(s: String, psiElement: PsiElement): DomElement? {
        val xmlAttributeName = getXmlAttributeName(psiElement) ?: return null
        val mapper = DomUtil.findDomElement(psiElement, Mapper::class.java) ?: return null
        return when (xmlAttributeName) {
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