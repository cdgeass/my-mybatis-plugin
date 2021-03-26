package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.util.xml.ConvertContext
import com.intellij.util.xml.Converter
import com.intellij.util.xml.CustomReferenceConverter
import com.intellij.util.xml.GenericDomValue
import io.github.cdgeass.codeInsight.reference.MyPsiClassReference
import io.github.cdgeass.codeInsight.reference.resolvePsiClass

/**
 * @author cdgeass
 * @since 2021/3/19
 */
class MyPsiClassConverter : Converter<PsiClass>(), CustomReferenceConverter<PsiClass> {

    override fun toString(t: PsiClass?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiClass? {
        if (s.isNullOrBlank() || context?.referenceXmlElement == null) {
            return null
        }

        return resolvePsiClass(context.referenceXmlElement!!)
    }

    override fun createReferences(
        value: GenericDomValue<PsiClass>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(MyPsiClassReference(element))
    }
}