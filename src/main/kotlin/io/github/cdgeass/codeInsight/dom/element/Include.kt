package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.MyDomElementConverter

/**
 * @author cdgeass
 * @since 2021/3/21
 */
interface Include : DomElement {

    @Convert(MyDomElementConverter::class)
    fun getRefid(): GenericAttributeValue<Sql>
}