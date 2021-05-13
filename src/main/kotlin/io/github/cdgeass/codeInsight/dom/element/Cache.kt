// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * mybatis-3-mapper.dtd:cache interface.
 * @author cdgeass
 */
interface Cache : DomElement {

    /**
     * Returns the value of the size child.
     * Attribute size
     * @return the value of the size child.
     */
    fun getSize(): GenericAttributeValue<String>

    /**
     * Returns the value of the flushInterval child.
     * Attribute flushInterval
     * @return the value of the flushInterval child.
     */
    fun getFlushInterval(): GenericAttributeValue<String>

    /**
     * Returns the value of the type child.
     * Attribute type
     * @return the value of the type child.
     */
    @Attribute("type")
    @Convert(MyPsiClassConverter::class)
    fun getType(): GenericAttributeValue<PsiClass>

    /**
     * Returns the value of the blocking child.
     * Attribute blocking
     * @return the value of the blocking child.
     */
    fun getBlocking(): GenericAttributeValue<String>

    /**
     * Returns the value of the eviction child.
     * Attribute eviction
     * @return the value of the eviction child.
     */
    fun getEviction(): GenericAttributeValue<String>

    /**
     * Returns the value of the readOnly child.
     * Attribute readOnly
     * @return the value of the readOnly child.
     */
    fun getReadOnly(): GenericAttributeValue<String>

    /**
     * Returns the list of property children.
     * @return the list of property children.
     */
    fun getProperties(): List<Property>

    /**
     * Adds new child to the list of property children.
     * @return created child
     */
    fun addProperty(): Property
}
