package io.github.cdgeass.editor.dom.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomManager;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
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
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        var position = parameters.getPosition();

                        if (hasPrefix(position)) {
                            var text = position.getText();
                            if (text.startsWith(PREFIX)) {
                                text = StringUtils.removeEnd(StringUtils.removeStart(text, PREFIX), CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);
                                addParamKeyWords(parameters, result.withPrefixMatcher(text));
                            } else {
                                addParamKeyWords(parameters, result);
                            }
                        }
                    }
                }
        );
    }

    private static boolean hasPrefix(PsiElement position) {
        var text = position.getText();
        return text.startsWith(PREFIX) ||
                text.startsWith(PREPARED_PREFIX);
    }

    private void addParamKeyWords(CompletionParameters parameters,
                                  CompletionResultSet result) {
        var position = parameters.getPosition();
        Map<String, PsiMethod> psiMethodMap = getPsiMethodMap(parameters);
        if (psiMethodMap.isEmpty()) {
            return;
        }

        var statement = (XmlTag) PsiTreeUtil.findFirstParent(position, parent -> {
            if (parent instanceof XmlTag) {
                return psiMethodMap.containsKey(((XmlTag) parent).getAttributeValue("id"));
            }
            return false;
        });

        var text = position.getText();
        var execLevel = StringUtils.countMatches(text, ".");

        assert statement != null;
        var psiMethod = psiMethodMap.get(statement.getAttributeValue("id"));
        var psiParameters = psiMethod.getParameterList().getParameters();
        for (var psiParameter : psiParameters) {
            var paramAnnotation = psiParameter.getAnnotation(StringConstants.PARAM_ANNOTATION);
            if (paramAnnotation != null) {
                var annotationParameterList = paramAnnotation.getParameterList();
                if (annotationParameterList.getAttributes().length > 0) {
                    var attribute = annotationParameterList.getAttributes()[0];
                    var keyword = "";
                    if (attribute.getLiteralValue() != null) {
                        keyword += attribute.getLiteralValue();
                        result.addElement(LookupElementBuilder.create(keyword).withIcon(PlatformIcons.FIELD_ICON));
                    }

                    if (!(psiParameter.getType() instanceof PsiPrimitiveType)) {
                        addKeyWord(execLevel, result, (PsiClassReferenceType) psiParameter.getType(), keyword);
                    }
                }
            }
        }
    }

    private void addKeyWord(int execLevel, CompletionResultSet result, PsiClassReferenceType psiType, String keyword) {
        if (execLevel <= 0) {
            return;
        }

        var paramClass = psiType.resolve();
        if (paramClass == null) {
            return;
        }

        Map<String, PsiType> filedTypeMap = new HashMap<>();

        var paramMethods = paramClass.getAllMethods();
        for (var paramMethod : paramMethods) {
            if (!ParamUtil.isGetter(paramMethod.getName()) ||
                    !(paramMethod.getReturnTypeElement() instanceof PsiClassReferenceType)) {
                continue;
            }

            filedTypeMap.put(ParamUtil.methodToProperty(paramMethod.getName()), paramMethod.getReturnType());
        }

        var paramFields = paramClass.getAllFields();
        for (var paramField : paramFields) {
            filedTypeMap.put(paramField.getName(), paramField.getType());
        }

        filedTypeMap.forEach((field, type) -> {
            result.addElement(LookupElementBuilder.create(keyword + "." + field).withIcon(PlatformIcons.FIELD_ICON));
            if (type instanceof PsiClassReferenceType) {
                addKeyWord(execLevel - 1, result, (PsiClassReferenceType) type, keyword + "." + field);
            }
        });
    }

    @NotNull
    private Map<String, PsiMethod> getPsiMethodMap(CompletionParameters parameters) {
        var project = parameters.getEditor().getProject();
        if (project == null) {
            return new HashMap<>(0);
        }
        var domManager = DomManager.getDomManager(project);

        var position = parameters.getPosition();
        var xmlFile = PsiTreeUtil.getParentOfType(position.getParent(), XmlFile.class);
        var fileElement = domManager.getFileElement(xmlFile, Mapper.class);
        if (fileElement == null) {
            return new HashMap<>(0);
        }

        var mapper = fileElement.getRootElement();
        var statements = new ArrayList<Statement>();
        statements.addAll(mapper.getSelects());
        statements.addAll(mapper.getDeletes());
        statements.addAll(mapper.getInserts());
        statements.addAll(mapper.getUpdates());

        return statements.stream()
                .map(statement -> {
                    var idAttributeValue = statement.getId();
                    if (idAttributeValue == null) {
                        return null;
                    }
                    return idAttributeValue.getValue();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(PsiMethod::getName, Function.identity()));
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}
