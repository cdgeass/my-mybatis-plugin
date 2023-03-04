package io.github.cdgeass.codeInsight.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import io.github.cdgeass.codeInsight.MyLiteralExpressionReferenceProvider

/**
 * @author cdgeass
 * @since 2021-11-05
 */
class MyLiteralExpressionReferenceSet(
    private val myElement: PsiElement,
    private val myStartInElement: Int,
    private val myText: String,
    private val myProvider: MyLiteralExpressionReferenceProvider
) {

    private var myReferences: Array<MyLiteralExpressionReference>

    init {
        myReferences = reparse()
    }

    /**
     * 将表达式调用拆分转换成 reference
     */
    private fun reparse(): Array<MyLiteralExpressionReference> {
        val length = myText.length

        val references = mutableListOf<MyLiteralExpressionReference>()
        var index = 0
        var offset = 0
        var lastOffset = 0
        while (offset < length) {
            val char = myText[offset]
            if (char == '.') {
                val subExpression = myText.substring(lastOffset, offset)
                val textRange = TextRange(myStartInElement + lastOffset, myStartInElement + offset)
                val reference = MyLiteralExpressionReference(
                    this,
                    myElement,
                    textRange,
                    subExpression,
                    index++,
                    myProvider
                )
                references.add(reference)
                lastOffset = offset + 1
            } else if (offset == length - 1) {
                val subExpression = myText.substring(lastOffset, offset + 1)
                val textRange = TextRange(myStartInElement + lastOffset, myStartInElement + offset + 1)
                val reference = MyLiteralExpressionReference(
                    this,
                    myElement,
                    textRange,
                    subExpression,
                    index++,
                    myProvider
                )
                references.add(reference)
            }
            offset++
        }

        return references.toTypedArray()
    }

    fun getElement(): PsiElement {
        return myElement
    }

    fun getContextReference(index: Int): MyLiteralExpressionReference {
        return myReferences[index]
    }

    fun getReferences(): Array<MyLiteralExpressionReference> {
        return myReferences
    }
}