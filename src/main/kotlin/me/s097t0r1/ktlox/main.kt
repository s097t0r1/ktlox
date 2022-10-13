package me.s097t0r1.ktlox

fun main(args: Array<String>) {
    if (args.size > 1) {
        System.err.println("Too many arguments")
    } else if (args.size == 1) {
        Ktlox.runFile(args[0])
    } else {
        Ktlox.runPrompt()
    }
}