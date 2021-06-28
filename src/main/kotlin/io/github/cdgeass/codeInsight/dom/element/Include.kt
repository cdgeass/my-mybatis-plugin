// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.converter.MyDomResolveConverter

/**
 * mybatis-3-mapper.dtd:include interface.
 * Type include documentation
 * <pre>
 *  Dynamic
 * </pre>
 * @author cdgeass
 */
interface Include : DomElement {

    /**
     * Returns the value of the refid child.
     * Attribute refid
     * @return the value of the refid child.
     */
    @Required
    @Attribute("refid")
    @Convert(MyDomResolveConverter::class)
    fun getRefid(): GenericAttributeValue<Sql>

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
