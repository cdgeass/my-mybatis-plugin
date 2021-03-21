package io.github.cdgeass.codeInsight.dom.element

import com.intellij.util.xml.SubTagList

/**
 * @author cdgeass
 * @since 2021/3/21
 */
interface Insert : Statement {

    @SubTagList("selectKey")
    fun getSelectKeys(): List<Statement>
}