package com.szw.finiteautomaton

import java.util.*

open class CollectionNFA(other: FiniteAutomation) : NFA(other) {
    private val fromLetterMap: MutableMap<Int, MutableMap<Char, MutableSet<Int>>> = HashMap()

    init {
        for ((from, letter, to) in transitions) {
            val letterMap = fromLetterMap[from] ?: HashMap<Char, MutableSet<Int>>().apply { fromLetterMap[from] = this }
            val toSet: MutableSet<Int> = letterMap[letter] ?: HashSet<Int>().apply { letterMap.set(letter, this) }
            toSet += to
        }
    }

    private fun eClosure(fromState: Int): Set<Int> = move(fromState, letterNull)

    private fun eClosure(fromStates: Set<Int>): Set<Int> {
        val eClosure: MutableSet<Int> = mutableSetOf<Int>().apply { addAll(fromStates) }
        val stack = ArrayDeque(fromStates)
        while (stack.isNotEmpty()) {
            val state = stack.pop()
            for (toState in move(state, letterNull)) {
                if (!eClosure.contains(toState)) {
                    eClosure.add(toState)
                    stack.add(toState)
                }
            }
        }
        return eClosure
    }

    private fun move(fromState: Int, letter: Char): Set<Int> =
        fromLetterMap[fromState]?.get(letter) ?: Collections.emptySet()

    private fun move(fromStates: Collection<Int>, letter: Char): Set<Int> =
        HashSet<Int>().also { toSet ->
            fromStates.forEach {
                toSet.addAll(move(it, letter))
            }
        }

    /**
     * NFA 确定化，使用子集构造法实现
     */
    fun determine(): CollectionDFA {
        val builder = Builder<Set<Int>>()
        val dStates = IdentifySetStack()
        dStates.push(setOf(this.startState))

        while (dStates.isNotEmpty()) {
            val state = dStates.pop()
            for (letter in this.letterList) {
                val eClosure = eClosure(move(state, letter))
                if (!dStates.contains(eClosure)) {
                    dStates.push(eClosure)
                }
                builder.addTransition(state, letter, eClosure)
            }
        }
        return CollectionDFA(builder.build())
    }

    class IdentifySetStack {
        private val setStack = ArrayDeque<Set<Int>>()
        private val setSet = HashSet<BitSet>()

        fun pop(): Set<Int> = setStack.pop().also { setSet.remove(getIdentify(it)) }

        fun push(set: Set<Int>) = setStack.push(set).also { setSet.add(getIdentify(set)) }

        fun contains(set: Set<Int>) = setSet.contains(getIdentify(set))

        private fun getIdentify(set: Set<Int>) = BitSet().also { bitset -> set.forEach { bitset[it] = true } }

        fun isNotEmpty() = setSet.isNotEmpty()
    }
}