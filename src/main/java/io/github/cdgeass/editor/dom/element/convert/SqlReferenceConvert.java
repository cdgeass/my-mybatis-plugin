package io.github.cdgeass.editor.dom.element.convert;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Sql;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public class SqlReferenceConvert extends Converter<Sql> implements CustomReferenceConverter<Sql> {

    @Nullable
    @Override
    public Sql fromString(@Nullable String s, ConvertContext context) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        var sqlTags = DomUtil.findByNameInNamespace(DomUtil.getContainingFileNameSpace(context.getFile()),
                context.getProject(), StringConstants.SQL);
        var sqlList = sqlTags.stream()
                .map(xmlTag -> {
                    var domElement = DomUtil.getDomElement(xmlTag);
                    if (!(domElement instanceof Sql)) {
                        return null;
                    }
                    return (Sql) domElement;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (var sql : sqlList) {
            var idAttributeValue = sql.getId();
            var id = idAttributeValue.getValue();
            if (StringUtils.equals(id, s)) {
                return sql;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable Sql sql, ConvertContext context) {
        if (sql == null) {
            return null;
        }
        var idAttributeValue = sql.getId();
        if (idAttributeValue == null) {
            return null;
        }
        return idAttributeValue.getValue();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<Sql> value, PsiElement element, ConvertContext context) {
        var sql = value.getValue();
        if (sql == null || sql.getXmlTag() == null || sql.getXmlTag().getAttribute(StringConstants.ID) == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return new PsiReference[]{
                new XmlReference<>(element,
                        Collections.singletonList(sql.getXmlTag().getAttribute(StringConstants.ID)))
        };
    }
}
