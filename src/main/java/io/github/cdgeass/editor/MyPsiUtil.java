package io.github.cdgeass.editor;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import lombok.experimental.UtilityClass;

/**
 * @author linqihang
 * @since 2020-09-21
 */
@UtilityClass
public class MyPsiUtil {

    public PsiType getGenericType(PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }

        var genericsResolveResult = ((PsiClassType) psiType).resolveGenerics();
        var substitutionMap = genericsResolveResult.getSubstitutor().getSubstitutionMap();
        if (substitutionMap.size() != 1) {
            return null;
        }

        return substitutionMap.values().stream().findFirst().get();
    }
}
