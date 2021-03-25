// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Required

/**
 * mybatis-3-mapper.dtd:bind interface.
 * @author cdgeass
 */
interface Bind : DomElement {

	/**
	 * Returns the value of the name child.
	 * Attribute name
	 * @return the value of the name child.
	 */
	@Required
	fun getName(): GenericAttributeValue<String>

	/**
	 * Returns the value of the value child.
	 * Attribute value
	 * @return the value of the value child.
	 */
	@Required
	fun getValue(): GenericAttributeValue<String>

}
