// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement

/**
 * mybatis-3-mapper.dtd:choose interface.
 * @author cdgeass
 */
interface Choose : DomElement {

    /**
     * Returns the list of when children.
     * @return the list of when children.
     */
    fun getWhens(): List<When>

    /**
     * Adds new child to the list of when children.
     * @return created child
     */
    fun addWhen(): When

    /**
     * Returns the value of the otherwise child.
     * @return the value of the otherwise child.
     */
    fun getOtherwise(): Otherwise
}
