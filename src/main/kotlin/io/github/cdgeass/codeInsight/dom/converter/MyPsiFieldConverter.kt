package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiReference
import com.intellij.util.xml.ConvertContext
import com.intellij.util.xml.Converter
import com.intellij.util.xml.CustomReferenceConverter
import com.intellij.util.xml.GenericDomValue

/**
 * @author cdgeass
 * @since 2021-03-26
 */
class MyPsiFieldConverter : Converter<PsiField>(), CustomReferenceConverter<PsiField> {

    override fun toString(t: PsiField?, context: ConvertContext?): String? {
        return t?.text
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiField? {
        TODO("Not yet implemented")
    }

    override fun createReferences(
        value: GenericDomValue<PsiField>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        TODO("Not yet implemented")
    }
}