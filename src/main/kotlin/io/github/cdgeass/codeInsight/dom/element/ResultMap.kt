package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface ResultMap : WithIdDomElement {

    @Attribute("type")
    @Convert(MyPsiClassConverter::class)
    fun getType(): GenericAttributeValue<PsiClass>

}