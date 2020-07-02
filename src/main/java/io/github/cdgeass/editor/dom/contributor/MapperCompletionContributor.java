package io.github.cdgeass.editor.dom.contributor;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.*;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
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

    private static final List<String> CONTAINS_PROPERTY = Lists.newArrayList("id", "result");

    public MapperCompletionContributor() {
        extend(
                CompletionType.BASIC,
                psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        var position = parameters.getPosition();
                        var xmlTag = (XmlTag) PsiTreeUtil.findFirstParent(position,
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

                        return;
                    }
                }
        );
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}
