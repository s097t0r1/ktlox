package me.s097t0r1.ktlox

sealed class Expression {

	abstract fun <R> accept(visitor: Visitor<R>): R

	interface Visitor<R> {
		fun visitBinaryExpression(expression: Binary): R
		fun visitUnaryExpression(expression: Unary): R
		fun visitGroupingExpression(expression: Grouping): R
		fun visitLiteralExpression(expression: Literal): R
	}

	class Binary(
		val left: Expression,
		val operator: Token,
		val right: Expression,
	) : Expression() {
		override fun <R> accept(visitor: Visitor<R>): R =
			visitor.visitBinaryExpression(this)
	}

	class Unary(
		val operator: Token,
		val right: Expression,
	) : Expression() {
		override fun <R> accept(visitor: Visitor<R>): R =
			visitor.visitUnaryExpression(this)
	}

	class Grouping(
		val expr: Expression,
	) : Expression() {
		override fun <R> accept(visitor: Visitor<R>): R =
			visitor.visitGroupingExpression(this)
	}

	class Literal(
		val value: Any?,
	) : Expression() {
		override fun <R> accept(visitor: Visitor<R>): R =
			visitor.visitLiteralExpression(this)
	}

}

