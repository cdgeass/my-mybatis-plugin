package io.github.cdgeass.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class PsiUtil {

    private PsiUtil() {
    }

    @Nullable
    public static PsiClass findClass(Project project, String qualifiedName) {
        var split = qualifiedName.split("\\.");
        var fileName = split[split.length - 1] + ".java";
        var psiFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
        for (PsiFile psiFile : psiFiles) {
            if (!(psiFile instanceof PsiJavaFile)) {
                continue;
            }

            var psiClass = (PsiClass) PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
            if (psiClass != null && qualifiedName.equals(psiClass.getQualifiedName())) {
                return psiClass;
            }
        }
        return null;
    }

    @Nullable
    public static PsiMethod findMethod(Project project, String qualifiedName) {
        var split = qualifiedName.split("\\.");
        var methodName = split[split.length - 1];
        var fileQualifiedName = qualifiedName.replace("." + methodName, "");

        var psiClass = findClass(project, fileQualifiedName);
        if (psiClass == null) {
            return null;
        }

        var psiMethods = PsiTreeUtil.findChildrenOfAnyType(psiClass, PsiMethod.class);
        for (PsiMethod psiMethod : psiMethods) {
            if (methodName.equals(psiMethod.getName())) {
                return psiMethod;
            }
        }

        var superClasses = psiClass.getSupers();
        for (PsiClass superClass : superClasses) {
            var superPsiMethods = PsiTreeUtil.findChildrenOfAnyType(superClass, PsiMethod.class);
            for (PsiMethod superPsiMethod : superPsiMethods) {
                if (methodName.equals(superPsiMethod.getName())) {
                    return superPsiMethod;
                }
            }
        }

        return null;
    }
}
