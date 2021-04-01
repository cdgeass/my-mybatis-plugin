package io.github.cdgeass.codeInsight

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlToken
import com.intellij.util.ProcessingContext
import io.github.cdgeass.codeInsight.reference.ParamReference
import java.util.regex.Pattern

/**
 * @author cdgeass
 * @since 2021-04-01
 */
class ExpressionReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val startOffset = if (element is XmlAttributeValue) 1 else 2

        val expression = if (element is XmlAttributeValue) {
            element.value
        } else if (element is XmlToken) {
            if (!element.text.startsWith("#{")) {
                return emptyArray()
            }
            element.text.removeSurrounding("#{", "}")
        } else {
            null
        }!!
        val length = expression.length

        val references = mutableListOf<PsiReference>()

        var index = 0
        var lastIndex = 0
        while (index < length) {
            val char = expression[index]
            if (char == ' ') {
                if (index != lastIndex) {
                    val subExpression = expression.substring(lastIndex, index)
                    references.addAll(convertToReferences(element, subExpression, startOffset + lastIndex))
                }
                // 跳过空格
                while (index < length && expression[index] == ' ') {
                    index++
                }
                lastIndex = index
            } else if (char == '!') {
                index++
                if (lastIndex == index - 1) {
                    lastIndex = index
                }
            } else {
                index++
            }
        }
        if (index != lastIndex) {
            val subExpression = expression.substring(lastIndex, index)
            references.addAll(convertToReferences(element, subExpression, startOffset + lastIndex))
        } else if (index == 0 || expression[index - 1] == ' ') {
            references.add(ParamReference(element, TextRange(index, index), ""))
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

    companion object {
        private val PATTERN: Pattern =
            Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*)(\\.|([a-zA-Z]|[a-zA-Z0-9])*|[(][)])*\$")
    }

}