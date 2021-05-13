package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiMethod
import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.NameValue
import com.intellij.util.xml.Required
import io.github.cdgeass.codeInsight.dom.converter.MyPsiMethodConverter

/**
 * @author cdgeass
 * @since 2021/3/19
 */
interface Statement : DomElement {
    /**
     * Returns the value of the id child.
     * Attribute id
     * @return the value of the id child.
     */
    @Required
    @NameValue
    @Convert(MyPsiMethodConverter::class)
    fun getId(): GenericAttributeValue<PsiMethod>
}
