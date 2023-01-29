package me.s097t0r1.ktlox.parser

import me.s097t0r1.ktlox.Expression
import me.s097t0r1.ktlox.Ktlox
import me.s097t0r1.ktlox.scanner.Token
import me.s097t0r1.ktlox.scanner.TokenType
import java.lang.RuntimeException

class Parser(private val tokens: List<Token>) {

    /*
     * expression -> equality
     * equality -> compare ( ( "==" | "!=" ) compare )*
     * compare -> term ( ( "<=" | ">=" | "<" | ">" ) term )*
     * term -> factor ( ( "+" | "-" ) ) factor)*
     * factor -> unary ( ( "*" | "/" ) ) unary)*
     * unary -> primary | ( ("!" | "-") primary)*
     * primary -> NUMBER | STRING | NIL | "true" | "false" | "(" expression ")"
     */

    private var index = 0

    fun parse(): Expression? {
        return try {
            expression()
        } catch (e: ParseException) {
            synchronize()
            null
        }
    }

    private fun synchronize() {
        forward()
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.CLASS, TokenType.FUN,
                TokenType.IF, TokenType.ELSE,
                TokenType.FOR, TokenType.WHILE,
                TokenType.VAR, TokenType.RETURN,
                TokenType.PRINT -> return
                else -> { /* no-op */ }
            }
            forward()
        }
    }

    private fun expression(): Expression {
        return equality(peek())
    }

    private fun equality(token: Token): Expression {
        val left = compare(token)
        while (peek().type == TokenType.EQUAL_EQUAL || peek().type == TokenType.BANG_EQUAL) {
            val operator = peek()
            forward()
            val right = compare(peek())
            return Expression.Binary(left, operator, right)
        }

        return left
    }

    private fun compare(token: Token): Expression {
        val left = term(token)
        while (isCompareToken(peek())) {
            val operator = peek()
            forward()
            val right = term(peek())
            return Expression.Binary(left, operator, right)
        }

        return left
    }

    private fun isCompareToken(token: Token) =
        token.type == TokenType.LESS || token.type == TokenType.LESS_EQUAL
                || token.type == TokenType.GREATER || token.type == TokenType.GREATER_EQUAL

    private fun term(token: Token): Expression {
        val left = factor(token)
        while (peek().type == TokenType.PLUS || peek().type == TokenType.MINUS) {
            val operator = peek()
            forward()
            val right = factor(peek())
            return Expression.Binary(left, operator, right)
        }

        return left
    }

    private fun factor(token: Token): Expression {
        val left = unary(token)
        while (peek().type == TokenType.STAR || peek().type == TokenType.SLASH) {
            val operator = peek()
            forward()
            val right = unary(peek())
            return Expression.Binary(left, operator, right)
        }

        return left
    }

    private fun unary(token: Token): Expression {
        while (token.type == TokenType.BANG || token.type == TokenType.MINUS) {
            val operator = token
            forward()
            val operand = primary(peek())
            return Expression.Unary(operator, operand)
        }

        return primary(token)
    }

    private fun primary(token: Token): Expression {
        val literal  = when (token.type) {
            TokenType.NUMBER -> Expression.Literal(token.literal)
            TokenType.STRING -> Expression.Literal(token.literal)
            TokenType.TRUE, TokenType.FALSE -> Expression.Literal(token.literal)
            TokenType.NIL -> Expression.Literal(token.literal)
            TokenType.LEFT_PAREN -> {
                forward()
                val expression = expression()
                require(TokenType.RIGHT_PAREN) {
                    "Missing ')' "
                }
                Expression.Grouping(expression)
            }
            else -> throw error(token, "Unexpected token")
        }
        forward()
        return literal
    }

    @Suppress("SameParameterValue")
    private fun require(type: TokenType, message: () -> String) {
        if (peek().type == type) return
        throw error(peek(), message())
    }

    private fun error(token: Token, message: String): ParseException {
        Ktlox.error(token.line, " at '" + token.lexeme + "'. " + message)
        return ParseException()
    }

    private fun forward() {
        if (!isAtEnd()) index++
    }

    private fun peek() = tokens[index]

    private fun previous() = tokens[index - 1]

    private fun isAtEnd(): Boolean = index >= tokens.lastIndex || peek().type == TokenType.EOF

    private class ParseException : RuntimeException()
}