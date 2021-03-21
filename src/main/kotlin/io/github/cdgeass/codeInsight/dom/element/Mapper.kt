package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.SubTagList

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface Mapper : DomElement {

    fun getNamespace(): GenericAttributeValue<PsiClass>

    @SubTagList("resultMap")
    fun getResultMaps(): List<ResultMap>

    @SubTagList("sql")
    fun getSqlList(): List<Sql>

    @SubTagList("select")
    fun getSelects(): List<Select>

    @SubTagList("delete")
    fun getDeletes(): List<Statement>

    @SubTagList("update")
    fun getUpdates(): List<Statement>

    @SubTagList("insert")
    fun getInserts(): List<Insert>

}