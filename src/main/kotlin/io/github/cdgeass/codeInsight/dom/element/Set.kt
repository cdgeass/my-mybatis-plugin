// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement

/**
 * mybatis-3-mapper.dtd:set interface.
 * @author cdgeass
 */
interface Set : DomElement {

    /**
     * Returns the value of the simple content.
     * @return the value of the simple content.
     */
    fun getValue(): String

    /**
     * Sets the value of the simple content.
     * @param value the new value to set
     */
    fun setValue(value: String)

    /**
     * Returns the list of include children.
     * Type include documentation
     * <pre>
     *  Dynamic
     * </pre>
     * @return the list of include children.
     */
    fun getIncludes(): List<Include>

    /**
     * Adds new child to the list of include children.
     * @return created child
     */
    fun addInclude(): Include

    /**
     * Returns the list of trim children.
     * @return the list of trim children.
     */
    fun getTrims(): List<Trim>

    /**
     * Adds new child to the list of trim children.
     * @return created child
     */
    fun addTrim(): Trim

    /**
     * Returns the list of where children.
     * @return the list of where children.
     */
    fun getWheres(): List<Where>

    /**
     * Adds new child to the list of where children.
     * @return created child
     */
    fun addWhere(): Where

    /**
     * Returns the list of set children.
     * @return the list of set children.
     */
    fun getSets(): List<Set>

    /**
     * Adds new child to the list of set children.
     * @return created child
     */
    fun addSet(): Set

    /**
     * Returns the list of foreach children.
     * @return the list of foreach children.
     */
    fun getForeaches(): List<Foreach>

    /**
     * Adds new child to the list of foreach children.
     * @return created child
     */
    fun addForeach(): Foreach

    /**
     * Returns the list of choose children.
     * @return the list of choose children.
     */
    fun getChooses(): List<Choose>

    /**
     * Adds new child to the list of choose children.
     * @return created child
     */
    fun addChoose(): Choose

    /**
     * Returns the list of if children.
     * @return the list of if children.
     */
    fun getIfs(): List<If>

    /**
     * Adds new child to the list of if children.
     * @return created child
     */
    fun addIf(): If

    /**
     * Returns the list of bind children.
     * @return the list of bind children.
     */
    fun getBinds(): List<Bind>

    /**
     * Adds new child to the list of bind children.
     * @return created child
     */
    fun addBind(): Bind
}
