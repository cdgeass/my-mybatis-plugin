package io.github.cdgeass.editor.dom;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;

import java.util.List;

/**
 * @author cdgass
 * @since 2020-05-21
 */
public interface Mapper extends DomElement {

    @Attribute("namespace")
    GenericAttributeValue<PsiClass> getNamespace();

    @NameValue
    List<Select> getSelects();
}
