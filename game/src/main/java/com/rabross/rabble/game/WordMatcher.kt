package com.rabross.rabble.game

import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_ABSENT
import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_CORRECT
import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_EMPTY
import com.rabross.rabble.game.WordMatcher.Companion.TOKEN_PRESENT
import kotlin.math.max

interface WordMatcher {

    companion object {
        const val TOKEN_CORRECT = 2
        const val TOKEN_PRESENT = 1
        const val TOKEN_ABSENT = 0
        const val TOKEN_EMPTY = -1
    }

    @Throws(IllegalArgumentException::class)
    suspend fun match(solution: String, guess: String): List<Int>
}

class WordMatcherImpl : WordMatcher {

    override suspend fun match(solution: String, guess: String): List<Int> {
        val adaptedGuess = guess.adaptSize(solution).joinToString("")
        val duplicateChecker = DuplicateChecker(solution)
        return correctPass(solution, adaptedGuess, duplicateChecker)
            .zip(presentPass(solution, adaptedGuess, duplicateChecker)) { v1, v2 -> max(v1, v2) }
    }

    private fun String.adaptSize(solution: String) = solution.mapIndexed { index, _ -> getOrNull(index)?.let { it } ?: " " }

    private fun correctPass(word: String, guess: String, duplicateChecker: DuplicateChecker) =
        guess.mapIndexed { i, char ->
            if (char.isCorrectAt(i, word)) TOKEN_CORRECT
                .also { duplicateChecker.seen(char) }
            else char.absentElseEmpty()
        }

    private fun presentPass(word: String, guess: String, duplicateChecker: DuplicateChecker) =
        guess.mapIndexed { i, char ->
            if (char.isPresentIn(word) && duplicateChecker.hasDuplicate(char) && char.isNotCorrectAt(i, word)) TOKEN_PRESENT
                .also { duplicateChecker.seen(char) }
            else char.absentElseEmpty()
        }

    private fun Char.isCorrectAt(index: Int, word: String) = word[index] == this
    private fun Char.isNotCorrectAt(index: Int, word: String) = word[index] != this
    private fun Char.isPresentIn(word: String) = word.contains(this)
    private fun Char.absentElseEmpty() = if(isWhitespace()) TOKEN_EMPTY else TOKEN_ABSENT

    class DuplicateChecker(solution: String) {

        private val duplicateMap = solution.groupingBy { char -> char }.eachCount().toMutableMap()

        fun hasDuplicate(char: Char): Boolean = duplicateMap[char]?.let { it > 0 } ?: false
        fun seen(char: Char) { duplicateMap[char] = duplicateMap.getValue(char).dec() }
    }
}