// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Required

/**
 * mybatis-3-mapper.dtd:parameter interface.
 * @author cdgeass
 */
interface Parameter : DomElement {

	/**
	 * Returns the value of the resultMap child.
	 * Attribute resultMap
	 * @return the value of the resultMap child.
	 */
	fun getResultMap(): GenericAttributeValue<String>

	/**
	 * Returns the value of the jdbcType child.
	 * Attribute jdbcType
	 * @return the value of the jdbcType child.
	 */
	fun getJdbcType(): GenericAttributeValue<String>

	/**
	 * Returns the value of the property child.
	 * Attribute property
	 * @return the value of the property child.
	 */
	@Required
	fun getProperty(): GenericAttributeValue<String>

	/**
	 * Returns the value of the typeHandler child.
	 * Attribute typeHandler
	 * @return the value of the typeHandler child.
	 */
	fun getTypeHandler(): GenericAttributeValue<String>

	/**
	 * Returns the value of the scale child.
	 * Attribute scale
	 * @return the value of the scale child.
	 */
	fun getScale(): GenericAttributeValue<String>

	/**
	 * Returns the value of the mode child.
	 * Attribute mode
	 * @return the value of the mode child.
	 */
	fun getMode(): GenericAttributeValue<String>

	/**
	 * Returns the value of the javaType child.
	 * Attribute javaType
	 * @return the value of the javaType child.
	 */
	fun getJavaType(): GenericAttributeValue<String>

}
