package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.NameValue
import com.intellij.util.xml.Required

/**
 * @author cdgeass
 * @since 2021/3/25
 */
interface WithIdDomElement : DomElement {

    /**
     * Returns the value of the id child.
     * Attribute id
     * @return the value of the id child.
     */
    @Required
    @NameValue
    fun getId(): GenericAttributeValue<String>
}