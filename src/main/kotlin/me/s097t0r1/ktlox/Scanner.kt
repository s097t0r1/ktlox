package me.s097t0r1.ktlox

class Scanner(
    private val source: String
) {

    private var start = 0
    private var current = 0
    private var line = 0

    private val tokens: MutableList<Token> = mutableListOf()

    private val identifiers = mapOf(
        "and" to TokenType.AND,
        "class" to TokenType.CLASS,
        "else" to TokenType.ELSE,
        "false" to TokenType.FALSE,
        "for" to TokenType.FOR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "nil" to TokenType.NIL,
        "or" to TokenType.OR,
        "print" to TokenType.PRINT,
        "return" to TokenType.RETURN,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "true" to TokenType.TRUE,
        "var" to TokenType.VAR,
        "while" to TokenType.WHILE,
    )

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        return tokens
    }

    private fun isAtEnd() = current >= source.length

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val lexem = source.substring(start, current)
        tokens.add(Token(type, lexem, literal, line))
    }

    private fun scanToken() {
        val symbol = forward()
        when (symbol) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '}' -> addToken(TokenType.LEFT_BRACE)
            '{' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '=' -> addToken(if (isNextCharacter('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (isNextCharacter('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (isNextCharacter('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '!' -> addToken(if (isNextCharacter('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '/' -> proccessSlash()
            '"' -> processStringLiteral()
            'o' -> if (isNextCharacter('r')) addToken(TokenType.OR)
            'a' -> if (isNextCharacter('r')) addToken(TokenType.OR)
            '\t', ' ', '\r' -> { /* Pass symbols */ }
            '\n' -> line++
            else -> {
                if (isDigit(symbol)) {
                    processDigit()
                } else if (isAlpha(symbol)) {
                    processIdentifier()
                } else {
                    Ktlox.error(line, "Unexpected symbol")
                }
            }
        }
    }

    private fun processIdentifier() {
        while (isAlpha(peek()) or isDigit(peek())) forward()
        val tokenType = identifiers[source.substring(start, current)] ?: TokenType.INDENTIFIER
        addToken(tokenType)
    }

    private fun isAlpha(symbol: Char): Boolean {
        return symbol in 'A'..'Z'
                || symbol in 'a'..'z'
                || symbol == '_'
    }

    private fun processDigit() {
        while (isDigit(peek()) && !isAtEnd()) forward()
        if (isDigit(peekNext()) || peek() == '.') {
            forward()
            while (isDigit(peek())) forward()
        }

        addToken(
            TokenType.NUMBER,
            source.substring(start, current)
                .toDoubleOrNull()
                ?: Ktlox.error(line, "Cannot convert literal to number type")
        )
    }

    private fun isDigit(symbol: Char) = symbol in '0'..'9'

    private fun processStringLiteral() {
        while ((peek() != '"') or !isAtEnd()) {
            if (peek() == '\n') line += 1
            forward()
        }

        if (isAtEnd()) {
            Ktlox.error(line, "End of string literal not found")
            return
        }

        addToken(
            TokenType.STRING,
            source.substring(start + 1, current)
        )
    }

    private fun proccessSlash() {
        if (isNextCharacter('/')) {
            while ((peek() != '\n') or !isAtEnd()) forward()
        } else {
            addToken(TokenType.SLASH)
        }
    }

    private fun isNextCharacter(char: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != char) return false

        return true.also { current++ }
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u000c'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 > source.length) return '\u000c'
        return source[current + 1]
    }

    private fun forward(): Char {
        return source[current++]
    }


}