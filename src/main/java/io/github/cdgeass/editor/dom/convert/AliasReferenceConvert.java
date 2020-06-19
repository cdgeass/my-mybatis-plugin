package io.github.cdgeass.editor.dom.convert;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since  2020-06-19
 */
public class AliasReferenceConvert extends Converter<String> implements CustomReferenceConverter<PsiClass> {

    @Nullable
    @Override
    public String fromString(@Nullable String s, ConvertContext context) {
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable String s, ConvertContext context) {
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        return new PsiReference[0];
    }
}
