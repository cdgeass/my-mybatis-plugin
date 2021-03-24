package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface ResultMap : DomElement {

    @NameValue
    fun getId(): GenericAttributeValue<String>

    @Attribute("type")
    @Convert(MyPsiClassConverter::class)
    fun getType(): GenericAttributeValue<PsiClass>

}