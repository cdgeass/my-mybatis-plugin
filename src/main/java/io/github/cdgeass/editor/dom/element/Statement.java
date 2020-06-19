package io.github.cdgeass.editor.dom.element;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import io.github.cdgeass.editor.dom.convert.PsiMethodReferenceConvert;

/**
 * @author cdgeass
 * @since 2020-05-26
 */
public interface Statement extends DomElement {

    @Convert(PsiMethodReferenceConvert.class)
    GenericAttributeValue<PsiMethod> getId();
}
