package io.github.cdgeass.editor.dom.element;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import io.github.cdgeass.editor.dom.convert.ResultMapReferenceConvert;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-05-22
 */
public interface ResultMap extends DomElement {

    GenericAttributeValue<String> getId();

    GenericAttributeValue<PsiClass> getType();

    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getExtends();

    GenericAttributeValue<Boolean> getAutoMapping();

    @SubTag("constructor")
    Constructor getConstructor();

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

    @SubTag("discriminator")
    Discriminator getDiscriminator();
}
