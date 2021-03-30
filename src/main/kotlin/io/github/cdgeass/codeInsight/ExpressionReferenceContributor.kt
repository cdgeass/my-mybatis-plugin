package io.github.cdgeass.codeInsight

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.ProcessingContext
import io.github.cdgeass.codeInsight.reference.ParamReference
import java.util.regex.Pattern

/**
 * @author cdgeass
 * @since 2021/3/28
 */
val PATTERN: Pattern = Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*)(\\.|([a-zA-Z]|[a-zA-Z0-9])*|[(][)])*\$")

class ExpressionReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute().withName("test")),
            ExpressionReferenceProvider()
        )
    }

    class ExpressionReferenceProvider : PsiReferenceProvider() {

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val expression = (element as XmlAttributeValue).value
            val length = expression.length

            val references = mutableListOf<PsiReference>()

            var index = 0
            var lastIndex = 0
            while (index < length) {
                val char = expression[index]
                if (char == ' ') {
                    if (index != lastIndex) {
                        val subExpression = expression.substring(lastIndex, index)
                        // psiElement TextRange 从 “ 开始 startOffset 需要 +1
                        references.addAll(convertToReferences(element, subExpression, 1 + lastIndex))
                    }
                    // 跳过空格
                    while (index < length && expression[index] == ' ') {
                        index++
                    }
                    lastIndex = index
                } else {
                    index++
                }
            }
            if (index != lastIndex) {
                val subExpression = expression.substring(lastIndex, index)
                references.addAll(convertToReferences(element, subExpression, 1 + lastIndex))
            }

            return references.toTypedArray()
        }

        private fun convertToReferences(
            element: PsiElement,
            expression: String,
            startOffset: Int
        ): List<PsiReference> {
            val matcher = PATTERN.matcher(expression)
            if (!matcher.find()) return emptyList()

            val references = mutableListOf<ParamReference>()

            val length = expression.length
            var index = 0
            var lastIndex = 0
            while (index < length) {
                val char = expression[index]
                if (char == '.') {
                    if (index != lastIndex) {
                        references.add(
                            ParamReference(
                                element,
                                TextRange(startOffset + lastIndex, startOffset + index),
                                expression.substring(0, lastIndex)
                            )
                        )
                    }
                    index++
                    lastIndex = index
                } else {
                    index++
                }
            }
            if (index != lastIndex) {
                references.add(
                    ParamReference(
                        element,
                        TextRange(startOffset + lastIndex, startOffset + index),
                        expression.substring(0, lastIndex)
                    )
                )
            }

            return references
        }

    }

}