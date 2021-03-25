package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.reference.MyXmlElementReference
import io.github.cdgeass.codeInsight.util.resolveElementReferences

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

        val referenceElements = resolveElementReferences(context.referenceXmlElement!!)
        return if (referenceElements.isEmpty()) null else referenceElements[0]
    }

    override fun createReferences(
        value: GenericDomValue<DomElement>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(MyXmlElementReference(element))
    }

}