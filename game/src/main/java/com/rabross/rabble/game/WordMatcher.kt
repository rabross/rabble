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
            correctPass(solution, attempt).zip(
                presentPass(solution, attempt, DuplicateChecker(solution))
            ) { v1, v2 -> max(v1, v2) }
        } else throw IllegalArgumentException("Attempt must match solution size")
    }

    private fun correctPass(word: String, attempt: String) =
        attempt.mapIndexed { i, char -> if (word[i] == char) TOKEN_CORRECT else TOKEN_ABSENT }

    private fun presentPass(word: String, attempt: String, duplicateChecker: DuplicateChecker) =
        attempt.map { char -> if (word.contains(char) && !duplicateChecker.isDuplicate(char)) TOKEN_PRESENT else TOKEN_ABSENT }

    class DuplicateChecker(private val seedChars: String) {
        private val seenChars = mutableListOf<Char>()
        fun isDuplicate(char: Char) =
            seenChars.count { seen -> seen == char } >= seedChars.count { seed -> seed == char }.also { seenChars.add(char) }
    }
}