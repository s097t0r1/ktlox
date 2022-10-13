package me.s097t0r1.ktlox

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
            hadError = false
        }
    }

    private fun run(sourceCode: String) {
        val tokens = Scanner(sourceCode).scanTokens()
        for (token in tokens) {
            println(token)
        }
    }

    fun error(line: Int, message: String) {
        System.err.println("Line $line: $message")
    }

}