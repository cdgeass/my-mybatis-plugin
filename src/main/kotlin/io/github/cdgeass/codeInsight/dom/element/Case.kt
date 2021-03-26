// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.converter.MyDomElementConverter
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * mybatis-3-mapper.dtd:case interface.
 * @author cdgeass
 */
interface Case : DomElement {

	/**
	 * Returns the value of the resultType child.
	 * Attribute resultType
	 * @return the value of the resultType child.
	 */
	@Attribute("resultType")
	@Convert(MyPsiClassConverter::class)
	fun getResultType(): GenericAttributeValue<PsiClass>

	/**
	 * Returns the value of the resultMap child.
	 * Attribute resultMap
	 * @return the value of the resultMap child.
	 */
	@Attribute("resultMap")
	@Convert(MyDomElementConverter::class)
	fun getResultMap(): GenericAttributeValue<ResultMap>

	/**
	 * Returns the value of the value child.
	 * Attribute value
	 * @return the value of the value child.
	 */
	@Required
	fun getValue(): GenericAttributeValue<String>

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
