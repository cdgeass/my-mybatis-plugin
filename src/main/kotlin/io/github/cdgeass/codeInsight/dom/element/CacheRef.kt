// Generated on Fri Mar 26 00:14:00 CST 2021
// DTD/Schema  :    mybatis-3-mapper.dtd

package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.*

/**
 * mybatis-3-mapper.dtd:cache-ref interface.
 * @author cdgeass
 */
interface CacheRef : DomElement {

    /**
     * Returns the value of the namespace child.
     * Attribute namespace
     * @return the value of the namespace child.
     */
    @Required
    @Attribute("namespace")
    @Resolve(Cache::class)
    fun getNamespace(): GenericAttributeValue<Cache>
}
