package com.szw.finiteautomaton

import kotlin.properties.Delegates


/**
 * 有限状态机定义
 */
abstract class FiniteAutomation private constructor(
    /**
     * ‘空’ 字符的表示
     */
    val states: Set<Int>,
    /**
     * 转换函数集合
     */
    val startState: Int,
    /**
     * 输入符号集合（字母表）
     */
    val finalStates: Set<Int>,
    /**
     * 结束状态（终态）
     */
    val letterList: Set<Char>,
    /**
     * 开始状态（始态）
     */
    val transitions: Set<Transition>,
    /**
     * 全部状态集合
     */
    val letterNull: Char = 0.toChar(),
) {
    constructor(other: FiniteAutomation) : this(
        other.states,
        other.startState,
        other.finalStates,
        other.letterList,
        other.transitions,
        other.letterNull,
    )

    companion object {
        fun <S> builder(build: Builder<S>.() -> Unit): FiniteAutomation =
            Builder<S>().apply(build).build()
    }

    class Builder<S>(letterNull: Char = 0.toChar()) {
        val stateMap: MutableMap<S, Int> = HashMap()
        var startState by Delegates.notNull<Int>()
        val finalStates: Set<Int> = HashSet()
        val letterList: Set<Char> = HashSet()
        val transitions: MutableSet<Transition> = HashSet()
        var letterNull: Char = letterNull
            private set

        // start with 0
        private var stateIncrId: Int = 0

        fun addTransition(rawFromState: S, letter: Char, rawToState: S) =
            addTransition(
                stateMap[rawFromState] ?: stateIncrId++.also { stateMap[rawFromState] = it },
                letter,
                stateMap[rawToState] ?: stateIncrId++.also { stateMap[rawToState] = it },
            )


        private fun addTransition(fromState: Int, letter: Char, toState: Int) {
            transitions += Transition(fromState, letter, toState)
        }

        fun build(): FiniteAutomation =
            object : FiniteAutomation(
                stateMap.values.toSet(),
                startState,
                finalStates,
                letterList,
                transitions,
                letterNull) {}
    }
}