package io.github.cdgeass.editor.dom;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.*;
import com.intellij.util.Function;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class XmlReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final List<PsiElement> targets;

    private final Function<PsiElement, String> getLookupString;

    public XmlReference(@NotNull PsiElement element, List<PsiElement> targets, Function<PsiElement, String> getLookupString) {
        super(element);
        this.targets = targets;
        this.getLookupString = getLookupString;
    }

    public XmlReference(@NotNull PsiElement element, List<PsiElement> targets) {
        super(element);
        this.targets = targets;
        this.getLookupString = PsiElement::getText;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return CollectionUtils.isEmpty(targets) ? new ResolveResult[0] : targets
                .stream()
                .map(PsiElementResolveResult::new)
                .toArray(ResolveResult[]::new);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        var resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return targets.stream()
                .map(psiElement -> LookupElementBuilder
                        .createWithSmartPointer(getLookupString.fun(psiElement), psiElement)
                        .withIcon(AllIcons.FileTypes.Xml))
                .toArray();
    }
}
