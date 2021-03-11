package io.github.cdgeass.formatter

import io.github.cdgeass.formatter.visitor.CustomStatementVisitor
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import org.apache.commons.lang3.RegExUtils
import org.apache.commons.lang3.tuple.MutablePair
import java.util.regex.Pattern

/**
 * @author cdgeass
 * @since 2021-03-03
 */
private const val PREPARING = "Preparing:"
private const val PARAMETERS = "Parameters:"

private var THREAD_NAME_PATTERN = Pattern.compile("\\[\\s?([a-zA-Z\\d-]+)\\s?]")
private val PARAMETER_TYPE_PATTERN = Pattern.compile("(.*)\\((\\S+)\\)")

private const val NULL = "null"

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

    val lines = text.split(LINE_SEPARATOR)

    val threadLogMap: MutableMap<String, MutablePair<String?, String?>> = mutableMapOf()
    var count = 0
    var suffix = 0
    lines.forEach { line ->
        var threadName = ""

        // 获取线程名, 用以多线程 sql 和参数匹配 无法获取线程名则计数进行匹配
        val matcher = THREAD_NAME_PATTERN.matcher(line)
        if (matcher.find()) {
            threadName = matcher.group(1)
            // 判断线程名是否重复, 如果重复则 suffix++
            if (threadLogMap.containsKey(threadName)) {
                suffix++
                val logPair = threadLogMap[threadName]!!
                if (line.contains(PREPARING) && logPair.left != null) {
                    threadName = "$threadName-$suffix"
                } else if (line.contains(PARAMETERS) && logPair.right != null) {
                    threadName = "$threadName-$suffix"
                }
            }
        } else {
            // 如果是 Preparing 则计数 +1, 如果是 Parameters 则不加 以匹配上一条 Preparing
            if (line.contains(PREPARING)) {
                count++
                threadName = "$count"
            } else if (line.contains(PARAMETERS)) {
                threadName = "$count"
            }
        }

        if (line.contains(PREPARING)) {
            val preparingStr = line.substringAfter(PREPARING).trim()
            threadLogMap.compute(threadName) { _, logPair: MutablePair<String?, String?>? ->
                if (logPair == null) {
                    return@compute MutablePair(preparingStr, null)
                } else {
                    logPair.left = preparingStr
                    return@compute logPair
                }
            }
        } else if (line.contains(PARAMETERS)) {
            val parametersStr = line.substringAfter(PARAMETERS).trim()
            threadLogMap.compute(threadName) { _, logPair: MutablePair<String?, String?>? ->
                if (logPair == null) {
                    MutablePair(null, parametersStr)
                } else {
                    logPair.right = parametersStr
                    logPair
                }
            }
        }
    }

    return threadLogMap.map { entry ->
        val value = entry.value
        val preparingStr = value.left ?: ""

        if (!preparingStr.contains("?")) {
            format(preparingStr, emptyList())
        } else {
            val parameters = value.right?.split(",")?.map { it.trim() } ?: emptyList()
            format(preparingStr, parameters)
        }
    }
}

/**
 * 替换 log 中的 ？, 并使用 jsqlparser 进行格式化
 */
private fun format(preparing: String, parametersWithType: List<String>): String {
    var preparing = preparing

    if (preparing.isBlank() && parametersWithType.isEmpty()) {
        return ""
    }

    val parameterStrings = parameters(parametersWithType)
    for (parameterString in parameterStrings) {
        preparing = RegExUtils.replaceFirst(preparing, "\\?", parameterString)
    }

    val statementVisitor = CustomStatementVisitor(0)
    return try {
        val statement = CCJSqlParserUtil.parse(preparing)
        statement.accept(statementVisitor)
        statementVisitor.toString()
    } catch (e: JSQLParserException) {
        preparing
    }
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