package com.rabross.rabble.game

interface GameConfig {
    val numberOfTries: Int
    val wordLength: Int
}

typealias MatchState = List<Int>

sealed class PlayState(val text: String) {
    data class Typing(val typing: String): PlayState(typing)
    data class Submit(val guess: String): PlayState(guess)
}

sealed class GameState {
    object Start: GameState()
    data class Current(val match: MatchState): GameState()
    data class Finish(val match: MatchState): GameState()
    data class Invalid(val reason: Exception): GameState()
}

class Game(private val wordProvider: WordProvider, private val wordMatcher: WordMatcher, private val gameConfig: GameConfig) {
    suspend fun play(state: PlayState): GameState {
        val guess = state.text
        return when(state) {
            is PlayState.Typing -> GameState.Current(buildList { repeat(gameConfig.wordLength){ add(-1) } })
            is PlayState.Submit -> when {
                gameConfig.isInvalid() -> GameState.Invalid(IllegalArgumentException("Invalid game config"))
                guess.isEmpty() -> GameState.Start
                guess.isIncorrectWordLength() -> GameState.Invalid(IllegalArgumentException("Attempts must be of length ${gameConfig.wordLength}"))
                guess.hasTooManyAttempts() -> GameState.Invalid(IllegalArgumentException("Too many attempts. Max attempts is ${gameConfig.numberOfTries}"))
                else -> {
                    val resultState = guess.chunked(gameConfig.wordLength).map { attempt -> wordMatcher.match(wordProvider.get(), attempt) }.flatten()
                    if (resultState.size < gameConfig.numberOfTries) GameState.Current(resultState)
                    else GameState.Finish(resultState)
                }
            }
        }
    }

    private fun GameConfig.isInvalid() = numberOfTries <= 0 || wordLength <= 0
    private fun String.hasTooManyAttempts() = length / gameConfig.wordLength > gameConfig.numberOfTries
    private fun String.isIncorrectWordLength() = length % gameConfig.wordLength != 0
}