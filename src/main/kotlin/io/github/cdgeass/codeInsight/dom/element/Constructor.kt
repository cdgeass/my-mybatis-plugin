// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.SubTagList

/**
 * mybatis-3-mapper.dtd:constructor interface.
 * @author cdgeass
 */
interface Constructor : DomElement {

    /**
     * Returns the list of idArg children.
     * @return the list of idArg children.
     */
    @SubTagList("idArg")
    fun getIdArgs(): List<IdArg>

    /**
     * Adds new child to the list of idArg children.
     * @return created child
     */
    @SubTagList("idArg")
    fun addIdArg(): IdArg

    /**
     * Returns the list of arg children.
     * @return the list of arg children.
     */
    fun getArgs(): List<Arg>

    /**
     * Adds new child to the list of arg children.
     * @return created child
     */
    fun addArg(): Arg
}
