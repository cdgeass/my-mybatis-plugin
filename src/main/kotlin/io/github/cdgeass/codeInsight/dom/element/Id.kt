// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import io.github.cdgeass.codeInsight.dom.converter.MyPsiClassConverter
import io.github.cdgeass.codeInsight.dom.converter.MyPsiFieldConverter

/**
 * mybatis-3-mapper.dtd:id interface.
 * @author cdgeass
 */
interface Id : DomElement {

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
    @Attribute("property")
    @Convert(MyPsiFieldConverter::class)
    fun getProperty(): GenericAttributeValue<PsiField>

    /**
     * Returns the value of the typeHandler child.
     * Attribute typeHandler
     * @return the value of the typeHandler child.
     */
    @Attribute("typeHandler")
    @Convert(MyPsiClassConverter::class)
    fun getTypeHandler(): GenericAttributeValue<PsiClass>

    /**
     * Returns the value of the javaType child.
     * Attribute javaType
     * @return the value of the javaType child.
     */
    @Attribute("javaType")
    @Convert(MyPsiClassConverter::class)
    fun getJavaType(): GenericAttributeValue<PsiClass>
}
