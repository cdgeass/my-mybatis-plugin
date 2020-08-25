package io.github.cdgeass.editor.dom.element.convert;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-29
 */
public class StatementReferenceConvert extends Converter<Statement> implements CustomReferenceConverter<Statement> {

    @Nullable
    @Override
    public Statement fromString(@Nullable String s, ConvertContext context) {
        if (s == null) {
            return null;
        }

        var mappers = DomUtil.findByNamespace(DomUtil.getContainingFileNameSpace(context.getFile()),
                context.getProject(), Mapper.class);
        List<Statement> statements = new ArrayList<>();
        for (var mapper : mappers) {
            statements.addAll(mapper.getDeletes());
            statements.addAll(mapper.getInserts());
            statements.addAll(mapper.getSelects());
            statements.addAll(mapper.getUpdates());
        }
        for (var statement : statements) {
            var idAttributeValue = statement.getId();
            var id = idAttributeValue.getValue();
            if (id != null && StringUtils.equals(id.getName(), s)) {
                return statement;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable Statement statement, ConvertContext context) {
        if (statement == null) {
            return null;
        }
        var idAttributeValue = statement.getId();
        if (idAttributeValue == null) {
            return null;
        }
        var psiMethod = idAttributeValue.getValue();
        if (psiMethod == null) {
            return null;
        }
        return psiMethod.getName();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<Statement> value, PsiElement element, ConvertContext context) {
        var statement = value.getValue();
        if (statement == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return new PsiReference[]{new XmlReference<>(element, Collections.singletonList(statement.getXmlTag()))};
    }
}
