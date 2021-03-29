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

            val references = mutableListOf<PsiReference>()
            var offset: Int
            var index = 0
            // TODO 处理空格
            while ((expression.indexOf(" ", index)
                    .also { offset = if (it == -1) expression.length else it }) != -1 || index == 0
            ) {
                var text = expression.substring(index, offset)
                val matcher = PATTERN.matcher(text)
                if (matcher.find()) {
                    // 删除方法调用的括号
                    text = text.replace("()", "").trim()

                    // 按 . 分割
                    var lastIndex = index
                    text.forEachIndexed { i, c ->
                        if (c == '.') {
                            references.add(
                                ParamReference(
                                    element,
                                    TextRange(index + lastIndex + 1, index + lastIndex + i + 1),
                                    expression.substring(0, index + lastIndex)
                                )
                            )
                            lastIndex = i + 1
                        }
                    }
                    if (lastIndex != text.length - 1) {
                        references.add(
                            ParamReference(
                                element,
                                TextRange(index + lastIndex + 1, index + text.length + 1),
                                expression.substring(0, index + lastIndex)
                            )
                        )
                    }
                }

                index = offset + 1
            }

            return references.toTypedArray()
        }

    }

}