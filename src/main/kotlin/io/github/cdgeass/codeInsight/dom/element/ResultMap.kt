package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.DomClassConverter

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface ResultMap : DomElement {

    fun getId(): GenericAttributeValue<String>

    @Convert(DomClassConverter::class)
    fun getType(): GenericAttributeValue<PsiClass>

}