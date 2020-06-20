package io.github.cdgeass.editor.dom.convert;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        XmlFile file = context.getFile();
        var domManager = DomManager.getDomManager(context.getProject());
        var fileElement = domManager.getFileElement(file, Mapper.class);
        if (fileElement == null) {
            return null;
        }
        var mapper = fileElement.getRootElement();
        List<Statement> statements = new ArrayList<>();
        statements.addAll(mapper.getDeletes());
        statements.addAll(mapper.getInserts());
        statements.addAll(mapper.getSelects());
        statements.addAll(mapper.getUpdates());
        for (var statement : statements) {
            var idAttributeValue = statement.getId();
            var id = idAttributeValue.getValue();
            if (id != null && StringUtils.equals(id.getName(), s)) {
                return statement;
            }
        }

        // find from other files with the same namespace
        var rootTag = file.getRootTag();
        if (rootTag == null) {
            return null;
        }
        var namespace = rootTag.getNamespace();
        var psiManager = PsiManager.getInstance(context.getProject());
        var virtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(context.getProject()));
        statements = virtualFiles
                .stream()
                .map(psiManager::findFile)
                .filter(Objects::nonNull)
                .map(virtualFile -> (XmlFile) virtualFile)
                .filter(xmlFile -> {
                    if (Objects.equals(file, xmlFile)) {
                        return false;
                    }
                    var xmlRootTag = xmlFile.getRootTag();
                    if (xmlRootTag == null) {
                        return false;
                    }
                    return Objects.equals(namespace, xmlRootTag.getNamespace());
                })
                .map(xmlFile -> {
                    var xmlFileElement = domManager.getFileElement(xmlFile, Mapper.class);
                    if (xmlFileElement == null) {
                        return null;
                    }
                    return xmlFileElement.getRootElement();
                })
                .filter(Objects::nonNull)
                .flatMap(tempMapper -> {
                    List<Statement> tempStatements = new ArrayList<>();
                    tempStatements.addAll(mapper.getDeletes());
                    tempStatements.addAll(mapper.getInserts());
                    tempStatements.addAll(mapper.getSelects());
                    tempStatements.addAll(mapper.getUpdates());
                    return tempStatements.stream();
                })
                .collect(Collectors.toList());
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
            return new PsiReference[0];
        }

        return new PsiReference[]{new XmlReference(element, Collections.singletonList(statement.getXmlTag()))};
    }
}
