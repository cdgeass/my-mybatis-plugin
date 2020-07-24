package io.github.cdgeass.editor.dom.contributor;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
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
import java.util.List;
import java.util.Map;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author cdgeass
 * @since 2020-07-09
 */
public class ParameterCompletionContributor extends CompletionContributor {

    private static final List<String> MAPPER_TAG_NAME = Lists.newArrayList(StringConstants.IF);

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

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);
        context.setDummyIdentifier("");
    }

    private void addKeyWords(CompletionParameters parameters, CompletionResultSet result) {
        var paramsMap = getParams(parameters);
        if (MapUtils.isEmpty(paramsMap)) {
            return;
        }

        boolean isParamPrefix = false;
        var position = parameters.getPosition();
        var parent = PsiTreeUtil.findFirstParent(position, psiElement ->
                psiElement instanceof XmlTag && MAPPER_TAG_NAME.contains(((XmlTag) psiElement).getName()));
        String text;
        if (parent == null) {
            text = position.getText();
        } else {
            var prevLeaf = PsiTreeUtil.prevLeaf(position);
            if (prevLeaf != null) {
                isParamPrefix = true;
                text = prevLeaf.getText();
            } else {
                return;
            }
        }

        if (text.endsWith(StringConstants.PARAM_SUFFIX)) {
            isParamPrefix = true;
            text = StringUtils.removeEnd(text, StringConstants.PARAM_SUFFIX);
        }
        if (text.startsWith(StringConstants.PARAM_PREFIX)) {
            text = StringUtils.removeStart(text, StringConstants.PARAM_PREFIX);
        } else if (text.startsWith(StringConstants.PREPARED_PARAM_PREFIX)) {
            text = StringUtils.removeStart(text, StringConstants.PREPARED_PARAM_PREFIX);
        }
        if (text.contains(StringConstants.WHITESPACE)) {
            text = text.substring(StringUtils.lastIndexOf(text, StringConstants.WHITESPACE));
        }
        if (text.endsWith(StringConstants.DOT)) {
            text += StringConstants.WHITESPACE;
        }

        var paramNames = StringUtils.split(text, StringConstants.DOT);
        if (paramNames == null || paramNames.length <= 0) {
            return;
        }

        addKeyWord(paramNames, 0, isParamPrefix, paramsMap, result);
    }

    private void addKeyWord(String[] paramNames,
                            int count,
                            boolean isParamPrefix,
                            Map<String, PsiType> paramsMap,
                            CompletionResultSet result) {
        int tempCount;
        for (var paramNameKey : paramsMap.keySet()) {
            tempCount = count;
            var paramName = paramNames[tempCount++].trim();
            var paramType = paramsMap.get(paramNameKey);

            if (paramNameKey.startsWith(paramName) && tempCount == paramNames.length) {
                if ("".equals(paramName)) {
                    result = result.withPrefixMatcher("");
                } else if (isParamPrefix) {
                    result = result.withPrefixMatcher(paramName);
                }
                result.addElement(LookupElementBuilder.create(paramNameKey).withIcon(PlatformIcons.PARAMETER_ICON));
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

                    addKeyWord(paramNames, tempCount, isParamPrefix, subParamsMap, result);
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
