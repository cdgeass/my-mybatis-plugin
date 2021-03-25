// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Required

/**
 * mybatis-3-mapper.dtd:parameterMap interface.
 * @author cdgeass
 */
interface ParameterMap : WithIdDomElement {

	/**
	 * Returns the value of the type child.
	 * Attribute type
	 * @return the value of the type child.
	 */
	@Required
	fun getType(): GenericAttributeValue<String>

	/**
	 * Returns the list of parameter children.
	 * @return the list of parameter children.
	 */
	fun getParameters(): List<Parameter>

	/**
	 * Adds new child to the list of parameter children.
	 * @return created child
	 */
	fun addParameter(): Parameter

}
