package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReference
import com.intellij.util.xml.ConvertContext
import com.intellij.util.xml.Converter
import com.intellij.util.xml.CustomReferenceConverter
import com.intellij.util.xml.GenericDomValue
import io.github.cdgeass.codeInsight.reference.MyPsiMethodReference
import io.github.cdgeass.codeInsight.reference.resolvePsiMethods

/**
 * @author cdgeass
 * @since 2021-03-18
 */
class MyPsiMethodConverter : Converter<PsiMethod>(), CustomReferenceConverter<PsiMethod> {

    override fun toString(t: PsiMethod?, context: ConvertContext?): String? {
        return t?.name
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiMethod? {
        if (s.isNullOrBlank() || context?.referenceXmlElement == null) {
            return null
        }

        val psiMethods = resolvePsiMethods(context.referenceXmlElement!!)
        return if (psiMethods.isEmpty()) {
            null
        } else {
            psiMethods[0]
        }
    }

    override fun createReferences(
        value: GenericDomValue<PsiMethod>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(MyPsiMethodReference(element))
    }
}
