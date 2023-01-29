package me.s097t0r1.ktlox

import me.s097t0r1.ktlox.parser.Parser
import me.s097t0r1.ktlox.scanner.Scanner
import me.s097t0r1.tools.AstPrinterVisitor
import java.io.File
import kotlin.system.exitProcess

object Ktlox {

    private var hadError: Boolean = false

    fun runFile(path: String) {
        val sourceCode = File(path).readText()
        run(sourceCode)
        if (hadError) exitProcess(65)
    }

    fun runPrompt() {
        while (true) {
            val line = readLine() ?: break
            run(line)
            println()
            hadError = false
        }
    }

    private fun run(sourceCode: String) {
        val tokens = Scanner(sourceCode).scanTokens()
        Parser(tokens).parse()?.accept(AstPrinterVisitor())
    }

    fun error(line: Int, message: String) {
        System.err.println("Line $line: $message")
    }

}