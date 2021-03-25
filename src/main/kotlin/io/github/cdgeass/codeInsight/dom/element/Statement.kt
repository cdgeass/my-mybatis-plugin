package io.github.cdgeass.codeInsight.dom.element

import com.intellij.psi.PsiMethod
import com.intellij.util.xml.*
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