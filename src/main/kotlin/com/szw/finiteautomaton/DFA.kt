package com.szw.finiteautomaton

abstract class DFA(other: FiniteAutomation) : FiniteAutomation(other) {

    init {
        for ((from, letter, to) in transitions) {
            this.addTransition(from, letter, to)
        }
    }

    abstract fun addTransition(from: Int, letter: Char, to: Int)
}