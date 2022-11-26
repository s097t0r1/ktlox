package me.s097t0r1.tools

import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <filename>")
        exitProcess(64)
    }
    defineAst(
        outputDir = args[0],
        fileName = "Expression",
        types = listOf(
            "Binary -> left: Expression, operator: Token, right: Expression",
            "Unary -> operator: Token, right: Expression",
            "Grouping -> expr: Expression",
            "Literal -> value: Any?"
        )
    )
}

@Suppress("SameParameterValue")
private fun defineAst(outputDir: String, fileName: String, types: List<String>) {
    val path = "$outputDir/$fileName.kt"
    PrintWriter(path).use { writer ->
        defineImports(writer)
        defineBody(writer, fileName, types)
    }
}

private fun defineBody(writer: PrintWriter, baseClass: String, types: List<String>) {
    writer.println("sealed class $baseClass {")
    writer.println()

    writer.println("\tabstract fun <R> accept(visitor: Visitor<R>): R")
    defineVisitor(writer, baseClass, types)
    defineTypes(writer, baseClass, types)
    writer.println("}")
    writer.println()
}

private fun defineVisitor(writer: PrintWriter, baseClass: String, types: List<String>) {
    writer.println("\tinterface Visitor<R> {")
    for (type in types) {
        val typeName = type.split("->")[0].trim()
        writer.print("\t\tfun visit$typeName$baseClass(")
        writer.print(baseClass.lowercase() + ": $typeName")
        writer.println("): R")
    }
    writer.println("\t}")
    writer.println()
}

private fun defineTypes(writer: PrintWriter, baseClass: String, types: List<String>) {
    for (type in types) {
        val (className, members) = type.removeWhiteSpaces().split("->")
        writer.println("\tclass $className(")
        for (member in members.split(',')) {
            val (variableName, typeOfVariable) = member.split(':')
            writer.println("\t\tval $variableName: $typeOfVariable,")
        }
        writer.println("\t) : $baseClass() {")
        writer.println("\t\toverride fun <R> accept(visitor: Visitor<R>): R =")
        writer.print("\t\t\tvisitor.visit$className$baseClass")
        writer.println("(this)")
        writer.println("\t}")
        writer.println()
    }
}

private fun defineImports(writer: PrintWriter) {
    writer.println("package me.s097t0r1.ktlox")
    writer.println()
    writer.println("import me.s097t0r1.ktlox.Token")
    writer.println()
}

private fun String.removeWhiteSpaces() = this.replace(" ", "")