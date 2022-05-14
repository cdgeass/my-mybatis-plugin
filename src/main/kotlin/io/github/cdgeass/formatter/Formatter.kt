package io.github.cdgeass.formatter

import org.apache.commons.lang3.tuple.MutablePair
import java.util.regex.Pattern

/**
 * @author cdgeass
 * @since 2021-03-03
 */
private const val PREPARING = "Preparing:"
private const val PARAMETERS = "Parameters:"

private val PARAMETER_TYPE_PATTERN = Pattern.compile("(.*)\\((\\S+)\\)")

private const val NULL = "null"

data class LinkedNode(
    val value: MutablePair<String, String>,
    var next: LinkedNode?
)

/**
 * 判断所选字符串是否是 mybatis log
 */
fun canFormat(text: String): Boolean {
    return text.contains(PREPARING) && text.contains(PARAMETERS)
}

/**
 * 将 mybatis log 格式化
 */
fun format(text: String): List<String> {
    if (!canFormat(text)) return emptyList()

    val sqlList = mutableListOf<String>()

    var curr: LinkedNode? = null
    var last: LinkedNode? = null
    text.split(LINE_SEPARATOR)
        .filter { line -> line.contains(PREPARING) || line.contains(PARAMETERS) }
        .forEach { line ->
            if (line.contains(PREPARING)) {
                val preparingStr = line.substringAfter(PREPARING).trim()
                if (curr == null) {
                    curr = LinkedNode(MutablePair(preparingStr, null), null)
                    last = curr
                } else {
                    curr!!.next = LinkedNode(MutablePair(preparingStr, null), null)
                    if (last == null) {
                        // 防止中间缺失 Preparing
                        last = curr
                    }
                    curr = curr!!.next
                }
            } else if (line.contains(PARAMETERS)) {
                val parametersStr = line.substringAfter(PARAMETERS).trim()
                while (last?.value?.right != null) {
                    last = last!!.next
                }
                if (last != null) {
                    last!!.value.right = parametersStr
                    val parameters = last!!.value.right
                        // 兼容 json
                        .replace("null", "null()")
                        .split("), ")
                        .map {
                            (it.trim() + ")").replace("null()", "null")
                        }
                        .map {
                            it.replace("'", "''")
                        }
                    val sql = format(last!!.value.left, parameters)
                    sqlList.add(sql)
                }
            }
        }

    last = last?.next
    while (last != null) {
        val value = last!!.value
        val preparingStr = value.left ?: continue

        val sql = format(preparingStr, emptyList())
        sqlList.add(sql)

        last = last!!.next
    }

    return sqlList
}

/**
 * 替换 log 中的 ？
 */
private fun format(preparing: String, parametersWithType: List<String>): String {
    var preparing = preparing

    if (preparing.isBlank() && parametersWithType.isEmpty()) {
        return ""
    }

    val parameterStrings = parameters(parametersWithType)
    for (parameterString in parameterStrings) {
        preparing = preparing.replaceFirst("?", parameterString)
    }

    return "$preparing;"
}

/**
 * 提取 Parameters 中的参数
 */
private fun parameters(parametersWithType: List<String>): List<String> {
    val parameters: MutableList<String> = mutableListOf()

    if (parametersWithType.isEmpty()) {
        return parameters
    }

    for (parameterWithType in parametersWithType) {
        if (NULL == parameterWithType) {
            parameters.add(NULL)
            continue
        }
        val matcher = PARAMETER_TYPE_PATTERN.matcher(parameterWithType)
        if (matcher.find()) {
            val parameter = matcher.group(1) ?: ""
            when (matcher.group(2)) {
                "Byte", "Long", "Short", "Integer", "Double", "Float", "Boolean" -> parameters.add(parameter)
                else -> parameters.add("'$parameter'")
            }
        }
    }
    return parameters
}
