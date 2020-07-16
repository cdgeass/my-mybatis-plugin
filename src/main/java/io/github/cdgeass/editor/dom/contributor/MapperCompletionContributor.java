package io.github.cdgeass.editor.dom.contributor;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import io.github.cdgeass.constants.StringConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author cdgeass
 * @since 2020-07-02
 */
public class MapperCompletionContributor extends CompletionContributor {

    private static final List<String> CONTAINS_TYPE = Lists.newArrayList("resultMap");

    public MapperCompletionContributor() {
        extend(
                CompletionType.BASIC,
                psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        var position = parameters.getPosition();
                        var isContinue = position.getParent() != null && position.getParent() instanceof XmlAttributeValue
                                && position.getParent().getParent() != null && position.getParent().getParent() instanceof XmlAttribute;
                        if (!isContinue) {
                            return;
                        }

                        var attributeName = ((XmlAttribute) position.getParent().getParent()).getName();
                        if (StringConstants.COLUMN.equals(attributeName)) {
                            addColumnKeyWords(parameters, result);
                        }
                        if (StringConstants.PROPERTY.equals(attributeName)) {
                            addPropertyKeyWords(parameters, result);
                        }

                    }
                }
        );
    }

    private static void addPropertyKeyWords(CompletionParameters parameters, CompletionResultSet result) {
        var xmlTag = (XmlTag) PsiTreeUtil.findFirstParent(parameters.getPosition(),
                psiElement -> psiElement instanceof XmlTag && CONTAINS_TYPE.contains(((XmlTag) psiElement).getName()));
        if (xmlTag == null) {
            return;
        }

        var type = xmlTag.getAttributeValue(StringConstants.TYPE);
        if (type == null) {
            return;
        }
        var project = parameters.getEditor().getProject();
        if (project == null) {
            return;
        }

        var javaPsiFacade = JavaPsiFacade.getInstance(project);
        var psiClass = javaPsiFacade.findClass(type, GlobalSearchScope.allScope(project));
        if (psiClass == null) {
            return;
        }

        var psiFields = psiClass.getAllFields();
        for (var psiField : psiFields) {
            result.addElement(LookupElementBuilder.create(psiField.getName()));
        }
    }

    private static void addColumnKeyWords(CompletionParameters parameters, CompletionResultSet result) {
        var xmlTag = (XmlTag) PsiTreeUtil.findFirstParent(parameters.getPosition(),
                psiElement -> psiElement instanceof XmlTag && CONTAINS_TYPE.contains(((XmlTag) psiElement).getName()));
        if (xmlTag == null) {
            return;
        }

        var type = xmlTag.getAttributeValue(StringConstants.TYPE);
        if (type == null) {
            return;
        }
        var project = parameters.getEditor().getProject();
        if (project == null) {
            return;
        }

        var javaPsiFacade = JavaPsiFacade.getInstance(project);
        var psiClass = javaPsiFacade.findClass(type, GlobalSearchScope.allScope(project));
        if (psiClass == null) {
            return;
        }

        var psiFields = psiClass.getAllFields();
        for (var psiField : psiFields) {
            result.addElement(LookupElementBuilder.create(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, psiField.getName())));
        }
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}
