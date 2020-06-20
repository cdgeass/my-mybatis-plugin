package io.github.cdgeass.editor.dom.element.configuration;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public interface TypeAlias extends DomElement {

    GenericAttributeValue<PsiClass> getType();

    GenericAttributeValue<String> getAlias();
}
