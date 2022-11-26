package me.s097t0r1.tools

import me.s097t0r1.ktlox.Expression

class AstPrinterVisitor : Expression.Visitor<String> {

    private fun parenthesize(name: String, vararg expr: Expression) = buildString {
        print("($name")
        for (exp in expr) {
            print(" ")
            print(exp.accept(this@AstPrinterVisitor))
        }
        print(")")
    }

    override fun visitBinaryExpression(expression: Expression.Binary): String =
        parenthesize(expression.operator.lexeme, expression.left, expression.right)

    override fun visitUnaryExpression(expression: Expression.Unary): String =
        parenthesize(expression.operator.lexeme, expression.right)

    override fun visitGroupingExpression(expression: Expression.Grouping): String =
        parenthesize("grouping", expression.expr)

    override fun visitLiteralExpression(expression: Expression.Literal): String =
        if (expression.value == null) "nil" else expression.value.toString()
}