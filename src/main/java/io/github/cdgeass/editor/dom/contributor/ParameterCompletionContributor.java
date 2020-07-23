package io.github.cdgeass.editor.dom.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericAttributeValue;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.element.mapper.Statement;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author cdgeass
 * @since 2020-07-09
 */
public class ParameterCompletionContributor extends CompletionContributor {


    public ParameterCompletionContributor() {
        extend(
                CompletionType.BASIC,
                psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        addKeyWords(parameters, result);
                    }
                }
        );
    }

    private void addKeyWords(CompletionParameters parameters, CompletionResultSet result) {
        var paramsMap = getParams(parameters);
        if (MapUtils.isEmpty(paramsMap)) {
            return;
        }

        var position = parameters.getPosition();
        var text = position.getText();
        if (text.endsWith(StringConstants.PARAM_SUFFIX)) {
            text = StringUtils.removeEnd(text, StringConstants.PARAM_SUFFIX);
        }
        text = StringUtils.removeEnd(text, CompletionUtilCore.DUMMY_IDENTIFIER);
        if (text.startsWith(StringConstants.PARAM_PREFIX)) {
            text = StringUtils.removeStart(text, StringConstants.PARAM_PREFIX);
        } else if (text.startsWith(StringConstants.PREPARED_PARAM_PREFIX)) {
            text = StringUtils.removeStart(text, StringConstants.PREPARED_PARAM_PREFIX);
        }
        if (text.contains(StringConstants.WHITESPACE)) {
            text = text.substring(StringUtils.lastIndexOf(text, StringConstants.WHITESPACE), text.length() - 1);
        }

        var paramNames = StringUtils.split(text, StringConstants.DOT);
        if (paramNames == null || paramNames.length <= 0) {
            return;
        }

        addKeyWord(paramNames, 0, paramsMap, result);
    }

    private void addKeyWord(String[] paramNames, int count, Map<String, PsiType> paramsMap, CompletionResultSet result) {
        for (var paramNameKey : paramsMap.keySet()) {
            var paramName = paramNames[count++];
            var paramType = paramsMap.get(paramNameKey);

            if (paramNameKey.startsWith(paramName)) {
                if (count == paramNames.length) {
                    result.withPrefixMatcher(paramName).addElement(LookupElementBuilder.create(paramName).withIcon(PlatformIcons.PARAMETER_ICON));
                } else if (paramNameKey.equals(paramName) && paramType instanceof PsiClassReferenceType) {
                    var paramClass = ((PsiClassReferenceType) paramType).resolve();
                    if (paramClass != null) {
                        var paramFields = paramClass.getAllFields();
                        var paramMethods = paramClass.getAllMethods();

                        Map<String, PsiType> subParamsMap = new HashMap<>();
                        for (var paramMethod : paramMethods) {
                            if (ParamUtil.isGetter(paramMethod.getName())) {
                                subParamsMap.put(ParamUtil.methodToProperty(paramMethod.getName()),
                                        paramMethod.getReturnType());
                            }
                        }
                        for (var paramField : paramFields) {
                            subParamsMap.put(paramField.getName(), paramField.getType());
                        }

                        addKeyWord(paramNames, count, subParamsMap, result);
                    }
                }
            }
        }
    }

    private Map<String, PsiType> getParams(CompletionParameters parameters) {
        var position = parameters.getPosition();
        var statementXmlTag = (XmlTag) PsiTreeUtil.findFirstParent(position,
                psiElement -> (psiElement instanceof XmlTag) && ((XmlTag) psiElement).getAttribute("id") != null);
        if (statementXmlTag == null) {
            return Collections.emptyMap();
        }

        var project = parameters.getEditor().getProject();
        if (project == null) {
            return Collections.emptyMap();
        }

        var domManager = DomManager.getDomManager(project);
        var domElement = domManager.getDomElement(statementXmlTag);
        if (!(domElement instanceof Statement)) {
            return Collections.emptyMap();
        }

        var statement = (Statement) domElement;
        GenericAttributeValue<PsiMethod> methodGenericAttributeValue = statement.getId();
        if (methodGenericAttributeValue == null || methodGenericAttributeValue.getValue() == null) {
            return Collections.emptyMap();
        }
        var method = methodGenericAttributeValue.getValue();
        var methodParameters = method.getParameterList().getParameters();

        var paramsMap = new HashMap<String, PsiType>(4);
        for (var methodParameter : methodParameters) {
            var paramAnnotation = methodParameter.getAnnotation(StringConstants.PARAM_ANNOTATION);
            if (paramAnnotation == null) {
                continue;
            }

            String name = null;
            if (paramAnnotation.getParameterList().getAttributes().length > 0) {
                name = paramAnnotation.getParameterList().getAttributes()[0].getLiteralValue();
            }

            paramsMap.put(name == null ? methodParameter.getName() : name, methodParameter.getType());
        }

        return paramsMap;
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet
            result) {
        super.fillCompletionVariants(parameters, result);
    }
}
