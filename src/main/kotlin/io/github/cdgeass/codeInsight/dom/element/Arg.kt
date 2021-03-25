// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue

/**
 * mybatis-3-mapper.dtd:arg interface.
 * @author cdgeass
 */
interface Arg : DomElement {

	/**
	 * Returns the value of the select child.
	 * Attribute select
	 * @return the value of the select child.
	 */
	fun getSelect(): GenericAttributeValue<String>

	/**
	 * Returns the value of the name child.
	 * Attribute name
	 * @return the value of the name child.
	 */
	fun getName(): GenericAttributeValue<String>

	/**
	 * Returns the value of the resultMap child.
	 * Attribute resultMap
	 * @return the value of the resultMap child.
	 */
	fun getResultMap(): GenericAttributeValue<String>

	/**
	 * Returns the value of the columnPrefix child.
	 * Attribute columnPrefix
	 * @return the value of the columnPrefix child.
	 */
	fun getColumnPrefix(): GenericAttributeValue<String>

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
	fun getJavaType(): GenericAttributeValue<String>

}
