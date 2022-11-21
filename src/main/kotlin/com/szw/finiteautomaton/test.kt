package com.szw.finiteautomaton

fun main() {
    val builder = FiniteAutomation.builder<Any> {
        addTransition(1, 'a', 2)
        addTransition(1, 'b', 3)
    }
}