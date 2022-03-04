package com.rabross.rabble.game

import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_ABSENT
import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_CORRECT
import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_PRESENT
import kotlin.math.max

interface WordMatcher {

    companion object {
        const val TOKEN_CORRECT = 2
        const val TOKEN_PRESENT = 1
        const val TOKEN_ABSENT = 0
    }

    @Throws(IllegalArgumentException::class)
    suspend fun match(solution: String, attempt: String): List<Int>
}

class WordMatcherImpl : WordMatcher {

    override suspend fun match(solution: String, attempt: String): List<Int> {
        return if (solution.length == attempt.length) {
            val duplicateChecker = DuplicateChecker(populateDuplicates(solution))
            correctPass(solution, attempt, duplicateChecker).zip(
                presentPass(solution, attempt, duplicateChecker)
            ) { v1, v2 -> max(v1, v2) }
        } else throw IllegalArgumentException("Attempt must match solution size")
    }

    private fun populateDuplicates(solution: String): MutableMap<Char, Int> {
        val duplicateMap = mutableMapOf<Char, Int>()
        solution.forEach {
            if (duplicateMap.containsKey(it)) duplicateMap[it] = duplicateMap.getValue(it).inc()
            else duplicateMap[it] = 1
        }
        return duplicateMap
    }

    private fun correctPass(word: String, attempt: String, duplicateChecker: DuplicateChecker) =
        attempt.mapIndexed { i, char ->
            if (word[i] == char) TOKEN_CORRECT.also { duplicateChecker.spotted(char) }
            else TOKEN_ABSENT
        }

    private fun presentPass(word: String, attempt: String, duplicateChecker: DuplicateChecker) =
        attempt.mapIndexed { i, char ->
            if (word.contains(char)
                && duplicateChecker.hasDuplicate(char)
                && word[i] != char
            ) TOKEN_PRESENT.also { duplicateChecker.spotted(char) }
            else TOKEN_ABSENT
        }

    class DuplicateChecker(private val duplicateMap: MutableMap<Char, Int>) {
        fun hasDuplicate(char: Char): Boolean = duplicateMap[char]?.let { it > 0 } ?: false
        fun spotted(char: Char) { duplicateMap[char] = duplicateMap.getValue(char).dec() }
    }
}