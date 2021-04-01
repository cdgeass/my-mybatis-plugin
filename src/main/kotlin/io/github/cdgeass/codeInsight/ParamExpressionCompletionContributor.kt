package io.github.cdgeass.codeInsight

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns

/**
 * @author cdgeass
 * @since 2021/4/1
 */
class ParamExpressionCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            ParamExpressionCompletionProvider()
        )
    }

}