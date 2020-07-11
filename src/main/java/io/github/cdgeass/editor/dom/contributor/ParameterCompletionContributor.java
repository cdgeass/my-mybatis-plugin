package io.github.cdgeass.editor.dom.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericAttributeValue;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author cdgeass
 * @since 2020-07-09
 */
public class ParameterCompletionContributor extends CompletionContributor {

    private static final String PREFIX = "${";
    private static final String PREPARED_PREFIX = "#{";

    public ParameterCompletionContributor() {
        extend(
                CompletionType.BASIC,
                psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        var position = parameters.getPosition();
                        if (!position.getText().startsWith(PREFIX) && !position.getText().startsWith(PREPARED_PREFIX)) {
                            return;
                        }

                        if (parameters.getEditor().getProject() == null) {
                            return;
                        }
                        var domManager = DomManager.getDomManager(parameters.getEditor().getProject());

                        var xmlFile = PsiTreeUtil.getParentOfType(position.getParent(), XmlFile.class);
                        var fileElement = domManager.getFileElement(xmlFile, Mapper.class);
                        if (fileElement == null) {
                            return;
                        }

                        var mapper = fileElement.getRootElement();
                        var statements = new ArrayList<Statement>();
                        statements.addAll(mapper.getSelects());
                        statements.addAll(mapper.getDeletes());
                        statements.addAll(mapper.getInserts());
                        statements.addAll(mapper.getUpdates());

                        var psiMethodMap = statements.stream()
                                .map(statement -> {
                                    var idAttributeValue = statement.getId();
                                    if (idAttributeValue == null) {
                                        return null;
                                    }

                                    return idAttributeValue.getValue();
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(PsiMethod::getName, Function.identity()));
                        var statement = (XmlTag) PsiTreeUtil.findFirstParent(position, parent -> {
                            if (parent instanceof XmlTag) {
                                return psiMethodMap.containsKey(((XmlTag) parent).getAttributeValue("id"));
                            }
                            return false;
                        });

                        assert statement != null;
                        var psiMethod = psiMethodMap.get(statement.getAttributeValue("id"));
                        var psiParameters = psiMethod.getParameterList().getParameters();
                        for (var psiParameter : psiParameters) {
                            var paramAnnotation = psiParameter.getAnnotation(StringConstants.PARAM_ANNOTATION);
                            if (paramAnnotation != null) {
                                var annotationParameterList = paramAnnotation.getParameterList();
                                for (var attribute : annotationParameterList.getAttributes()) {
                                    if (attribute.getLiteralValue() != null) {
                                        result.addElement(LookupElementBuilder.create(attribute.getLiteralValue()));
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}
