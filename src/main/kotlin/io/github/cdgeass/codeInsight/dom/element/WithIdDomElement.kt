package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.NameValue

/**
 * @author cdgeass
 * @since 2021/3/25
 */
interface WithIdDomElement : DomElement {

    @NameValue
    fun getId(): GenericAttributeValue<String>
}