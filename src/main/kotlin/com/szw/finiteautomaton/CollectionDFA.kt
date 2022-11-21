package com.szw.finiteautomaton

class CollectionDFA(other: FiniteAutomation) : DFA(other) {
    private val fromLetterMap: MutableMap<Int, MutableMap<Char, Int>> = HashMap()

    override fun addTransition(from: Int, letter: Char, to: Int) {
        val letterMap = fromLetterMap[from] ?: HashMap<Char, Int>().apply { fromLetterMap[from] = this }
        letterMap[letter] = to
    }

    fun minimize0() {

    }


    /**
     * DFA 最小化，使用分割法实现
     */
    fun minimize(): FiniteAutomation {
        val builder = Builder<Set<Int>>()

        val nonFinalStateSet = HashSet(this@CollectionDFA.states).apply { removeAll(this@CollectionDFA.finalStates) }
        // 全部都是终结状态的情况
        if (nonFinalStateSet.size <= 0) {
            for (letter in letterList) {
                builder.addTransition(finalStates, letter, finalStates)
            }
            return builder.build();
        }

        val divideMap = HashMap<Int, MutableSet<Int>>()
        val divisible = java.util.ArrayDeque<MutableSet<Int>>().apply { this.push(nonFinalStateSet) }
        val indivisible = ArrayList<MutableSet<Int>>()

        finalStates.toMutableSet().apply { this.forEach { divideMap[it] = this } }

        nonFinalStateSet.forEach { divideMap[it] = nonFinalStateSet }

        val first = nonFinalStateSet.first()
        for (letter in letterList) {
            val to = fromLetterMap[first]!![letter] ?: continue
            builder.addTransition(nonFinalStateSet, letter, divideMap[to]!!)
            break
        }

        while (divisible.isNotEmpty()) {
            val toBeDivided = divisible.peek()
            val newDivides = listOf<Set<Int>>()
            val oldSize = toBeDivided.size

            if (toBeDivided.size == 1) {
                continue
            }

            val iterator = toBeDivided.iterator()
            // 跟 first 状态不同类（即接收相同的输入，却转换到了不同类的状态）的状态将被放到新集合
            val first = iterator.next()

            var newDivide: MutableSet<Int>? = null
            // 遍历，找出与 first 不同类的状态
            while (iterator.hasNext()) {
                val next = iterator.next()
                // 考察每一个 letter，确定 next 跟 first 是否同类
                for (letter in letterList) {
                    val firstTo = fromLetterMap[first]?.get(letter)?.let { divideMap[it] }
                    val nextTo = fromLetterMap[next]?.get(letter)?.let { divideMap[it] }
                    if (firstTo !== nextTo) {
                        if (newDivide == null) {
                            // 有新的分类产生了，貌似“不可再分”的分类可能会变得再次可分
                            indivisible.forEach { divisible.push(it) }
                            newDivide = HashSet()
                            // 新的分类必须放到栈顶
                            divisible.push(newDivide)
                        }
                        newDivide.add(next)
                        divideMap[next] = newDivide
                        iterator.remove()
                    }
                }
            }

            // 当前类似乎“不可再分”了
            if (toBeDivided.size == oldSize) {
                indivisible.add(divisible.pop())
            }
        }

        for (divide in divideMap.values) {
            val first = divide.first()
            for (letter in letterList) {
                val firstTo = fromLetterMap[first]?.get(letter)?.let { divideMap[it] }
                if (firstTo !== null) {
                    builder.addTransition(divide, letter, firstTo)
                }
            }
        }

        return builder.build();
    }
}
