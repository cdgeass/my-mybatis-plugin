package io.github.cdgeass.codeInsight

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.GenericReferenceProvider
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.ProcessingContext
import io.github.cdgeass.codeInsight.reference.MyLiteralExpressionReferenceSet
import org.apache.commons.lang3.math.NumberUtils

/**
 * @author cdgeass
 * @since 2021-04-01
 */
class MyLiteralExpressionReferenceProvider : GenericReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val subExpressions = parse(element)
        return subExpressions.flatMap {
            MyLiteralExpressionReferenceSet(element, it.first, it.second, this)
                .getReferences().toList()
        }.toTypedArray()
    }

    /**
     * 将字符串拆分成表达式
     */
    private fun parse(element: PsiElement): List<Pair<Int, String>> {
        val startInElement = if (element is XmlAttributeValue) 1 else 0

        val text = getText(element) ?: return emptyList()
        val length = text.length

        val expressions = mutableListOf<Pair<Int, String>>()

        var index = 0
        var lastIndex = 0
        while (index < length) {
            val char = text[index]
            if (char == ' ' || char == ',' || char == ')' || char == '}' || char == '!' || char == '>' || char == '<' || char == '=') {
                if (index != lastIndex) {
                    val subExpression = text.substring(lastIndex, index)
                    if (isValid(subExpression)) {
                        expressions.add(Pair(startInElement + lastIndex, subExpression))
                    }
                }
                // 跳过空格
                while (index < length && (text[index] == ' ' || text[index] == ',' || text[index] == ')' || text[index] == '}' || text[index] == '!' || text[index] == '>' || text[index] == '<' || text[index] == '=')) {
                    index++
                }
                lastIndex = index
            } else if (char == '(' || char == '{' || char == '#' || char == '$') {
                index++
                lastIndex = index
            } else if (index == length - 1) {
                val subExpression = text.substring(lastIndex, index + 1)
                if (isValid(subExpression)) {
                    expressions.add(Pair(startInElement + lastIndex, subExpression))
                }
                index++
            } else {
                index++
            }
        }

        return expressions
    }

    /**
     * 获取 psi element 文本并判断合法性
     */
    private fun getText(element: PsiElement): String? {
        if (element is XmlAttributeValue) {
            return element.value
        }

        val text = element.text
        if (!text.contains("#{") && !text.contains("\${")) {
            var preElement = element.prevSibling
            while (preElement != null) {
                val preText = preElement.text
                if (!preText.contains("#{") && !preText.contains("\${")) {
                    preElement = preElement.prevSibling
                } else if (preText.contains("}")) {
                    return null
                } else {
                    return text
                }
            }
            return null
        } else {
            return text
        }
    }

    /**
     * 是否是合法表达式
     * 过滤 null
     * 过滤字符串
     * 过滤数字
     */
    private fun isValid(expression: String): Boolean {
        return when {
            expression == "null" -> false
            expression.startsWith("'") -> false
            expression == "and" || expression == "or" || expression == "&gt;" || expression == "&lt;" || expression == "&amp;" || expression == "&apos;" || expression == "&quot;" -> false
            NumberUtils.isCreatable(expression) -> false
            else -> true
        }
    }

}
