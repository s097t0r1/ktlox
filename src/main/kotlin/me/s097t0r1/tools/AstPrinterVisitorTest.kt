package me.s097t0r1.tools

import me.s097t0r1.ktlox.Expression
import me.s097t0r1.ktlox.Token
import me.s097t0r1.ktlox.TokenType

fun main() {
    val expression: Expression = Expression.Binary(
        Expression.Grouping(
            Expression.Binary(
                Expression.Literal(1),
                Token(TokenType.PLUS, "+", null, 1),
                Expression.Literal(2)
            )
        ),
        Token(TokenType.STAR, "*", null, 1),
        Expression.Grouping(
            Expression.Binary(
                Expression.Literal(4),
                Token(TokenType.MINUS, "-", null, 1),
                Expression.Literal(3)
            )
        )
    )

    println(expression.accept(RPNAstPrinterVisitor()))
}