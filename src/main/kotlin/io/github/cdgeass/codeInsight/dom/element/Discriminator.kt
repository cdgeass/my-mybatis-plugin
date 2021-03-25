// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Required

/**
 * mybatis-3-mapper.dtd:discriminator interface.
 * @author cdgeass
 */
interface Discriminator : DomElement {

	/**
	 * Returns the value of the jdbcType child.
	 * Attribute jdbcType
	 * @return the value of the jdbcType child.
	 */
	fun getJdbcType(): GenericAttributeValue<String>

	/**
	 * Returns the value of the column child.
	 * Attribute column
	 * @return the value of the column child.
	 */
	fun getColumn(): GenericAttributeValue<String>

	/**
	 * Returns the value of the typeHandler child.
	 * Attribute typeHandler
	 * @return the value of the typeHandler child.
	 */
	fun getTypeHandler(): GenericAttributeValue<String>

	/**
	 * Returns the value of the javaType child.
	 * Attribute javaType
	 * @return the value of the javaType child.
	 */
	@Required
	fun getJavaType(): GenericAttributeValue<String>

	/**
	 * Returns the list of case children.
	 * @return the list of case children.
	 */
	fun getCases(): List<Case>

	/**
	 * Adds new child to the list of case children.
	 * @return created child
	 */
	fun addCase(): Case

}
