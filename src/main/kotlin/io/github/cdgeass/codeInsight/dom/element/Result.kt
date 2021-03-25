// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue

/**
 * mybatis-3-mapper.dtd:result interface.
 * @author cdgeass
 */
interface Result : DomElement {

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
	 * Returns the value of the property child.
	 * Attribute property
	 * @return the value of the property child.
	 */
	fun getProperty(): GenericAttributeValue<String>

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
