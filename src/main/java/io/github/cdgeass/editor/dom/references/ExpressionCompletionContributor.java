package io.github.cdgeass.editor.dom.references;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.tree.injected.InjectedCaret;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.sql.dialects.generic.GenericDialect;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomManager;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.OGNLUtil;
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
public class ExpressionCompletionContributor extends CompletionContributor {

    public ExpressionCompletionContributor() {
        extend(
                CompletionType.BASIC,
                psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        if (isEnable(parameters)) {
                            addKeyWords(parameters, result);
                        }
                    }
                }
        );
    }

    private boolean isEnable(CompletionParameters parameters) {
        var position = getPosition(parameters);
        if (position == null) {
            return false;
        }
        var text = position.getText();
        if (text.startsWith(StringConstants.PREPARED_PARAM_PREFIX) || text.startsWith(StringConstants.PARAM_PREFIX)) {
            return true;
        }
        return PsiTreeUtil.findFirstParent(position, psiElement ->
                psiElement instanceof XmlAttribute && ((XmlAttribute) psiElement).getName().equals(StringConstants.TEST)) != null;
    }

    private void addKeyWords(CompletionParameters parameters, CompletionResultSet result) {
        var position = getPosition(parameters);
        if (position == null) {
            return;
        }
        var paramsMap = getParams(position);
        if (MapUtils.isEmpty(paramsMap)) {
            return;
        }

        var isParamPrefix = false;
        var text = position.getText();

        if (text.endsWith(StringConstants.PARAM_SUFFIX)) {
            isParamPrefix = true;
            text = StringUtils.removeEnd(text, StringConstants.PARAM_SUFFIX);
        }
        if (text.startsWith(StringConstants.PARAM_PREFIX)) {
            text = StringUtils.removeStart(text, StringConstants.PARAM_PREFIX);
        } else if (text.startsWith(StringConstants.PREPARED_PARAM_PREFIX)) {
            text = StringUtils.removeStart(text, StringConstants.PREPARED_PARAM_PREFIX);
        }

        if (text.endsWith(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
            text = StringUtils.removeEnd(text, CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);
        } else if (text.endsWith(CompletionUtilCore.DUMMY_IDENTIFIER)) {
            isParamPrefix = true;
            text = StringUtils.removeEnd(text, CompletionUtilCore.DUMMY_IDENTIFIER);
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
        result.stopHere();
    }

    private void addKeyWord(String[] paramNames,
                            int callCount,
                            boolean isParamPrefix,
                            Map<String, PsiType> paramsMap,
                            CompletionResultSet result) {
        int tempCallCount;
        for (var paramNameKey : paramsMap.keySet()) {
            tempCallCount = callCount;
            var paramName = paramNames[tempCallCount++].trim();
            var paramType = paramsMap.get(paramNameKey);

            if (paramNameKey.startsWith(paramName) && tempCallCount == paramNames.length) {
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
                        if (OGNLUtil.isGetter(paramMethod.getName())) {
                            subParamsMap.put(OGNLUtil.methodToProperty(paramMethod.getName()),
                                    paramMethod.getReturnType());
                        }
                    }
                    for (var paramField : paramFields) {
                        subParamsMap.put(paramField.getName(), paramField.getType());
                    }

                    addKeyWord(paramNames, tempCallCount, isParamPrefix, subParamsMap, result);
                }
            }
        }
    }

    private PsiElement getPosition(CompletionParameters parameters) {
        var position = parameters.getPosition();
        var editor = parameters.getEditor();

        var language = position.getLanguage();
        if (language instanceof XMLLanguage) {
            return position;
        } else if (language instanceof GenericDialect) {
            var virtualFile = position.getContainingFile().getVirtualFile();
            if (!(virtualFile instanceof VirtualFileWindow)) {
                return null;
            }
            virtualFile = ((VirtualFileWindow) virtualFile).getDelegate();
            var xmlFile = PsiManager.getInstance(position.getProject()).findFile(virtualFile);
            if (xmlFile == null) {
                return null;
            }

            var currentCaret = editor.getCaretModel().getCurrentCaret();
            if (currentCaret instanceof InjectedCaret) {
                currentCaret = ((InjectedCaret) currentCaret).getDelegate();
            }
            return xmlFile.findElementAt(currentCaret.getOffset());
        } else {
            return null;
        }
    }

    private Map<String, PsiType> getParams(PsiElement position) {
        var statementXmlTag = (XmlTag) PsiTreeUtil.findFirstParent(position,
                psiElement -> (psiElement instanceof XmlTag) && ((XmlTag) psiElement).getAttribute("id") != null);
        if (statementXmlTag == null) {
            return Collections.emptyMap();
        }

        var domManager = DomManager.getDomManager(position.getProject());
        var domElement = domManager.getDomElement(statementXmlTag);
        if (!(domElement instanceof Statement)) {
            return Collections.emptyMap();
        }

        var statement = (Statement) domElement;
        var methodGenericAttributeValue = statement.getId();
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