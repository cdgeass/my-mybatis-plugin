// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Required

/**
 * mybatis-3-mapper.dtd:collection interface.
 * @author cdgeass
 */
interface Collection : DomElement {

	/**
	 * Returns the value of the foreignColumn child.
	 * Attribute foreignColumn
	 * @return the value of the foreignColumn child.
	 */
	fun getForeignColumn(): GenericAttributeValue<String>

	/**
	 * Returns the value of the resultMap child.
	 * Attribute resultMap
	 * @return the value of the resultMap child.
	 */
	fun getResultMap(): GenericAttributeValue<String>

	/**
	 * Returns the value of the typeHandler child.
	 * Attribute typeHandler
	 * @return the value of the typeHandler child.
	 */
	fun getTypeHandler(): GenericAttributeValue<String>

	/**
	 * Returns the value of the ofType child.
	 * Attribute ofType
	 * @return the value of the ofType child.
	 */
	fun getOfType(): GenericAttributeValue<String>

	/**
	 * Returns the value of the fetchType child.
	 * Attribute fetchType
	 * @return the value of the fetchType child.
	 */
	fun getFetchType(): GenericAttributeValue<String>

	/**
	 * Returns the value of the columnPrefix child.
	 * Attribute columnPrefix
	 * @return the value of the columnPrefix child.
	 */
	fun getColumnPrefix(): GenericAttributeValue<String>

	/**
	 * Returns the value of the column child.
	 * Attribute column
	 * @return the value of the column child.
	 */
	fun getColumn(): GenericAttributeValue<String>

	/**
	 * Returns the value of the javaType child.
	 * Attribute javaType
	 * @return the value of the javaType child.
	 */
	fun getJavaType(): GenericAttributeValue<String>

	/**
	 * Returns the value of the autoMapping child.
	 * Attribute autoMapping
	 * @return the value of the autoMapping child.
	 */
	fun getAutoMapping(): GenericAttributeValue<String>

	/**
	 * Returns the value of the jdbcType child.
	 * Attribute jdbcType
	 * @return the value of the jdbcType child.
	 */
	fun getJdbcType(): GenericAttributeValue<String>

	/**
	 * Returns the value of the resultSet child.
	 * Attribute resultSet
	 * @return the value of the resultSet child.
	 */
	fun getResultSet(): GenericAttributeValue<String>

	/**
	 * Returns the value of the select child.
	 * Attribute select
	 * @return the value of the select child.
	 */
	fun getSelect(): GenericAttributeValue<String>

	/**
	 * Returns the value of the property child.
	 * Attribute property
	 * @return the value of the property child.
	 */
	@Required
	fun getProperty(): GenericAttributeValue<String>

	/**
	 * Returns the value of the notNullColumn child.
	 * Attribute notNullColumn
	 * @return the value of the notNullColumn child.
	 */
	fun getNotNullColumn(): GenericAttributeValue<String>

	/**
	 * Returns the value of the constructor child.
	 * @return the value of the constructor child.
	 */
	fun getConstructor(): Constructor

	/**
	 * Returns the list of id children.
	 * @return the list of id children.
	 */
	fun getIds(): List<Id>

	/**
	 * Adds new child to the list of id children.
	 * @return created child
	 */
	fun addId(): Id

	/**
	 * Returns the list of result children.
	 * @return the list of result children.
	 */
	fun getResults(): List<Result>

	/**
	 * Adds new child to the list of result children.
	 * @return created child
	 */
	fun addResult(): Result

	/**
	 * Returns the list of association children.
	 * @return the list of association children.
	 */
	fun getAssociations(): List<Association>

	/**
	 * Adds new child to the list of association children.
	 * @return created child
	 */
	fun addAssociation(): Association

	/**
	 * Returns the list of collection children.
	 * @return the list of collection children.
	 */
	fun getCollections(): List<Collection>

	/**
	 * Adds new child to the list of collection children.
	 * @return created child
	 */
	fun addCollection(): Collection

	/**
	 * Returns the value of the discriminator child.
	 * @return the value of the discriminator child.
	 */
	fun getDiscriminator(): Discriminator

}
