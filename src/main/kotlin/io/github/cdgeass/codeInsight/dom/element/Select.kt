package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.MyDomElementConverter
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * @author cdgeass
 * @since 2021/3/20
 */
interface Select : Statement {

    @Attribute("resultMap")
    @Convert(MyDomElementConverter::class)
    fun getResultMap(): GenericAttributeValue<ResultMap>

    @Attribute("resultType")
    @Convert(MyPsiClassConverter::class)
    fun getResultType(): GenericAttributeValue<PsiClass>
}