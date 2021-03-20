package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiMethod
import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.DomMethodConverter

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface Statement : DomElement {

    @Convert(DomMethodConverter::class)
    fun getId(): GenericAttributeValue<PsiMethod>

}