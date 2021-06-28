// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.Resolve
import io.github.cdgeass.codeInsight.dom.converter.MyDomResolveConverter
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter

/**
 * mybatis-3-mapper.dtd:select interface.
 * @author cdgeass
 */
interface Select : Statement {

    /**
     * Returns the value of the simple content.
     * @return the value of the simple content.
     */
    fun getValue(): String

    /**
     * Sets the value of the simple content.
     * @param value the new value to set
     */
    fun setValue(value: String)

    /**
     * Returns the value of the resultMap child.
     * Attribute resultMap
     * @return the value of the resultMap child.
     */
    @Attribute("resultMap")
    @Convert(MyDomResolveConverter::class)
    fun getResultMap(): GenericAttributeValue<ResultMap>

    /**
     * Returns the value of the useCache child.
     * Attribute useCache
     * @return the value of the useCache child.
     */
    fun getUseCache(): GenericAttributeValue<String>

    /**
     * Returns the value of the lang child.
     * Attribute lang
     * @return the value of the lang child.
     */
    fun getLang(): GenericAttributeValue<String>

    /**
     * Returns the value of the resultSets child.
     * Attribute resultSets
     * @return the value of the resultSets child.
     */
    fun getResultSets(): GenericAttributeValue<String>

    /**
     * Returns the value of the resultType child.
     * Attribute resultType
     * @return the value of the resultType child.
     */
    @Attribute("resultType")
    @Convert(MyPsiClassConverter::class)
    fun getResultType(): GenericAttributeValue<PsiClass>

    /**
     * Returns the value of the parameterType child.
     * Attribute parameterType
     * @return the value of the parameterType child.
     */
    @Attribute("parameterType")
    @Convert(MyPsiClassConverter::class)
    fun getParameterType(): GenericAttributeValue<PsiClass>

    /**
     * Returns the value of the statementType child.
     * Attribute statementType
     * @return the value of the statementType child.
     */
    fun getStatementType(): GenericAttributeValue<String>

    /**
     * Returns the value of the databaseId child.
     * Attribute databaseId
     * @return the value of the databaseId child.
     */
    fun getDatabaseId(): GenericAttributeValue<String>

    /**
     * Returns the value of the fetchSize child.
     * Attribute fetchSize
     * @return the value of the fetchSize child.
     */
    fun getFetchSize(): GenericAttributeValue<String>

    /**
     * Returns the value of the flushCache child.
     * Attribute flushCache
     * @return the value of the flushCache child.
     */
    fun getFlushCache(): GenericAttributeValue<String>

    /**
     * Returns the value of the resultSetType child.
     * Attribute resultSetType
     * @return the value of the resultSetType child.
     */
    fun getResultSetType(): GenericAttributeValue<String>

    /**
     * Returns the value of the timeout child.
     * Attribute timeout
     * @return the value of the timeout child.
     */
    fun getTimeout(): GenericAttributeValue<String>

    /**
     * Returns the value of the resultOrdered child.
     * Attribute resultOrdered
     * @return the value of the resultOrdered child.
     */
    fun getResultOrdered(): GenericAttributeValue<String>

    /**
     * Returns the list of include children.
     * Type include documentation
     * <pre>
     *  Dynamic
     * </pre>
     * @return the list of include children.
     */
    fun getIncludes(): List<Include>

    /**
     * Adds new child to the list of include children.
     * @return created child
     */
    fun addInclude(): Include

    /**
     * Returns the list of trim children.
     * @return the list of trim children.
     */
    fun getTrims(): List<Trim>

    /**
     * Adds new child to the list of trim children.
     * @return created child
     */
    fun addTrim(): Trim

    /**
     * Returns the list of where children.
     * @return the list of where children.
     */
    fun getWheres(): List<Where>

    /**
     * Adds new child to the list of where children.
     * @return created child
     */
    fun addWhere(): Where

    /**
     * Returns the list of set children.
     * @return the list of set children.
     */
    fun getSets(): List<Set>

    /**
     * Adds new child to the list of set children.
     * @return created child
     */
    fun addSet(): Set

    /**
     * Returns the list of foreach children.
     * @return the list of foreach children.
     */
    fun getForeaches(): List<Foreach>

    /**
     * Adds new child to the list of foreach children.
     * @return created child
     */
    fun addForeach(): Foreach

    /**
     * Returns the list of choose children.
     * @return the list of choose children.
     */
    fun getChooses(): List<Choose>

    /**
     * Adds new child to the list of choose children.
     * @return created child
     */
    fun addChoose(): Choose

    /**
     * Returns the list of if children.
     * @return the list of if children.
     */
    fun getIfs(): List<If>

    /**
     * Adds new child to the list of if children.
     * @return created child
     */
    fun addIf(): If

    /**
     * Returns the list of bind children.
     * @return the list of bind children.
     */
    fun getBinds(): List<Bind>

    /**
     * Adds new child to the list of bind children.
     * @return created child
     */
    fun addBind(): Bind
}
