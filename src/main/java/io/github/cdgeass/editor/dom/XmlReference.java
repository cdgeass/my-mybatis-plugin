package io.github.cdgeass.editor.dom;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class XmlReference<T extends PsiElement> extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final List<T> targets;

    private final List<T> variants;

    private final Function<T, String> getLookupString;

    public XmlReference(@NotNull PsiElement element, List<T> targets) {
        super(element, new TextRange(0, element.getText().length() - 1));
        this.targets = targets;
        this.getLookupString = PsiElement::getText;
        this.variants = targets;
    }

    public XmlReference(@NotNull PsiElement element, List<T> targets, List<T> variants) {
        super(element, new TextRange(0, element.getText().length() - 1));
        this.targets = targets;
        this.getLookupString = PsiElement::getText;
        this.variants = variants;
    }

    public XmlReference(@NotNull PsiElement element, List<T> targets, List<T> variants, Function<T, String> getLookupString) {
        super(element, new TextRange(0, element.getText().length() - 1));
        this.targets = targets;
        this.variants = variants;
        this.getLookupString = getLookupString;
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
        return resolveResults.length >= 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return variants.stream()
                .map(psiElement -> LookupElementBuilder
                        .createWithSmartPointer(getLookupString.apply(psiElement), psiElement)
                        .withIcon(AllIcons.FileTypes.Xml))
                .toArray();
    }

}
