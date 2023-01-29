package me.s097t0r1.tools

import me.s097t0r1.ktlox.Expression
import me.s097t0r1.ktlox.scanner.Token
import me.s097t0r1.ktlox.scanner.TokenType

fun main() {
    val expression: Expression = Expression.Binary(
        Expression.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expression.Literal(123)
        ),
        Token(TokenType.STAR, "*", null, 1),
        Expression.Grouping(
            Expression.Literal(45.67)
        )
    )

    println(expression.accept(AstPrinterVisitor()))
}