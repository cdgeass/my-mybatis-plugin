package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface Sql : DomElement {

    fun getId(): GenericAttributeValue<String>
}