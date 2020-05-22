package io.github.cdgeass.editor.dom;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;

import java.util.List;

/**
 * @author cdgass
 * @since 2020-05-21
 */
public interface Mapper extends DomElement {

    GenericAttributeValue<PsiClass> getNamespace();

    List<SelectStatement> getSelects();

    List<ModificationStatement> getInserts();

    List<ModificationStatement> getUpdates();

    List<ModificationStatement> getDeletes();
}
