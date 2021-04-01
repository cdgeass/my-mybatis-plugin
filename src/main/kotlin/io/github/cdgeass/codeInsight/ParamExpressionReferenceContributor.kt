package io.github.cdgeass.codeInsight

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.sql.psi.patterns.SqlPatterns

/**
 * @author cdgeass
 * @since 2021-04-01
 */
class ParamExpressionReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            SqlPatterns.psiElement(),
            ExpressionReferenceProvider()
        )
    }
}