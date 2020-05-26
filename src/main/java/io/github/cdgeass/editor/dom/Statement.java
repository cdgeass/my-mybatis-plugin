package io.github.cdgeass.editor.dom;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since 2020-05-26
 */
public interface Statement extends DomElement {

    @Convert(PsiJavaReferenceConvert.class)
    GenericAttributeValue<PsiMethod> getId();
}
