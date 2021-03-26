// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Required
import io.github.cdgeass.codeInsight.dom.converter.MyDomElementConverter
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * mybatis-3-mapper.dtd:resultMap interface.
 * @author cdgeass
 */
interface ResultMap : WithIdDomElement {

	/**
	 * Returns the value of the autoMapping child.
	 * Attribute autoMapping
	 * @return the value of the autoMapping child.
	 */
	fun getAutoMapping(): GenericAttributeValue<Boolean>

	/**
	 * Returns the value of the type child.
	 * Attribute type
	 * @return the value of the type child.
	 */
	@Required
	@Attribute("type")
	@Convert(MyPsiClassConverter::class)
	fun getType(): GenericAttributeValue<PsiClass>

	/**
	 * Returns the value of the extends child.
	 * Attribute extends
	 * @return the value of the extends child.
	 */
	@Attribute("extends")
	@Convert(MyDomElementConverter::class)
	fun getExtends(): GenericAttributeValue<ResultMap>

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
