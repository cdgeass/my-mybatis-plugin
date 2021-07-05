// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiClass
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.SubTagList
import com.intellij.util.xml.SubTagsList

/**
 * mybatis-3-mapper.dtd:mapper interface.
 * Type mapper documentation
 * <pre>
 *        Copyright 2009-2018 the original author or authors.
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *           http://www.apache.org/licenses/LICENSE-2.0
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 * </pre>
 * @author cdgeass
 */
interface Mapper : DomElement {

    /**
     * Returns the value of the namespace child.
     * Attribute namespace
     * @return the value of the namespace child.
     */
    fun getNamespace(): GenericAttributeValue<PsiClass>

    /**
     * Returns the list of cache-ref children.
     * @return the list of cache-ref children.
     */
    fun getCacheRefs(): List<CacheRef>

    /**
     * Adds new child to the list of cache-ref children.
     * @return created child
     */
    fun addCacheRef(): CacheRef

    /**
     * Returns the list of cache children.
     * @return the list of cache children.
     */
    fun getCaches(): List<Cache>

    /**
     * Adds new child to the list of cache children.
     * @return created child
     */
    fun addCache(): Cache

    /**
     * Returns the list of resultMap children.
     * @return the list of resultMap children.
     */
    @SubTagList("resultMap")
    fun getResultMaps(): List<ResultMap>

    /**
     * Adds new child to the list of resultMap children.
     * @return created child
     */
    @SubTagList("resultMap")
    fun addResultMap(): ResultMap

    /**
     * Returns the list of sql children.
     * @return the list of sql children.
     */
    fun getSqls(): List<Sql>

    /**
     * Adds new child to the list of sql children.
     * @return created child
     */
    fun addSql(): Sql

    /**
     * Returns the list of insert children.
     * @return the list of insert children.
     */
    fun getInserts(): List<Insert>

    /**
     * Adds new child to the list of insert children.
     * @return created child
     */
    fun addInsert(): Insert

    /**
     * Returns the list of update children.
     * @return the list of update children.
     */
    fun getUpdates(): List<Update>

    /**
     * Adds new child to the list of update children.
     * @return created child
     */
    fun addUpdate(): Update

    /**
     * Returns the list of delete children.
     * @return the list of delete children.
     */
    fun getDeletes(): List<Delete>

    /**
     * Adds new child to the list of delete children.
     * @return created child
     */
    fun addDelete(): Delete

    /**
     * Returns the list of select children.
     * @return the list of select children.
     */
    fun getSelects(): List<Select>

    /**
     * Adds new child to the list of select children.
     * @return created child
     */
    fun addSelect(): Select

    @SubTagsList(value = ["select", "update", "delete", "insert"])
    fun getStatements(): List<Statement>
}
