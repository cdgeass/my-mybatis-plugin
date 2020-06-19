package io.github.cdgeass.editor.dom.element;

import com.intellij.util.xml.*;
import io.github.cdgeass.editor.dom.convert.ResultMapReferenceConvert;
import io.github.cdgeass.editor.dom.convert.StatementReferenceConvert;
import org.bouncycastle.asn1.cmp.GenRepContent;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-06-19
 */
public interface Collection extends DomElement {

    GenericAttributeValue<String> getProperty();

    GenericAttributeValue<String> getColumn();

    GenericAttributeValue<String> getJavaType();

    GenericAttributeValue<String> getOfType();

    GenericAttributeValue<String> getJdbcType();

    @Convert(StatementReferenceConvert.class)
    GenericAttributeValue<SelectStatement> getSelect();

    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getResultMap();

    GenericAttributeValue<String> getTypeHandler();

    GenericAttributeValue<String> getNotNullColumn();

    GenericAttributeValue<String> getColumnPrefix();

    GenericAttributeValue<String> getResultSet();

    GenericAttributeValue<String> getForeignColumn();

    GenericAttributeValue<Boolean> getAutoMapping();

    GenericAttributeValue<String> getFetchType();

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
