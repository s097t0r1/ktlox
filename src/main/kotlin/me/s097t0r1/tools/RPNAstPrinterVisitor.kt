package me.s097t0r1.tools

import me.s097t0r1.ktlox.Expression

class RPNAstPrinterVisitor : Expression.Visitor<String> {

    private fun parenthesize(operator: String, vararg expr: Expression) = buildString {
        for (exp in expr) {
            append(exp.accept(this@RPNAstPrinterVisitor))
            append(" ")
        }
        append(operator)
    }

    override fun visitBinaryExpression(expression: Expression.Binary): String {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right)
    }

    override fun visitUnaryExpression(expression: Expression.Unary): String {
        return parenthesize(expression.operator.lexeme, expression.right)
    }

    override fun visitGroupingExpression(expression: Expression.Grouping): String {
        return expression.expr.accept(this@RPNAstPrinterVisitor)
    }

    override fun visitLiteralExpression(expression: Expression.Literal): String {
        return if (expression.value == null) "nil" else expression.value.toString()
    }

}