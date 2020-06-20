package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.*;
import io.github.cdgeass.editor.dom.convert.ResultMapReferenceConvert;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-06-19
 */
public interface Case extends DomElement {

    GenericAttributeValue<String> getValue();

    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getResultMap();

    GenericAttributeValue<String> getResultType();

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
