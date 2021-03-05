package io.github.cdgeass.formatter

import io.github.cdgeass.formatter.visitor.CustomStatementVisitor
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import org.apache.commons.lang3.RegExUtils
import java.util.regex.Pattern

/**
 * @author cdgeass
 * @since 2021-03-03
 */
private const val NULL: String = "null"
private val GET_PARAM_TYPE_PATTERN = Pattern.compile("(.*)\\((\\S+)\\)")

fun format(preparing: String, parametersWithType: List<String>): String {
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
        val matcher = GET_PARAM_TYPE_PATTERN.matcher(parameterWithType)
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