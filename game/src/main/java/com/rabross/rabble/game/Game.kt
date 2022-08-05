package com.rabross.rabble.game

interface GameConfig {
    val numberOfTries: Int get() = 1
    val wordLength: Int get() = 1
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

interface Game {
    suspend fun state(state: PlayState): GameState
}

class GameImpl(private val wordProvider: WordProvider, private val wordMatcher: WordMatcher, private val gameConfig: GameConfig) : Game {
    override suspend fun state(state: PlayState): GameState {
        val guess = state.text
        return when(state) {
            is PlayState.Typing -> GameState.Current(
                guess.chunked(gameConfig.wordLength).map { attempt ->
                    if(attempt.length == gameConfig.wordLength) wordMatcher.match(wordProvider.get(), attempt)
                    else buildList { repeat(gameConfig.wordLength){ add(-1) }}
            }.flatten())
            is PlayState.Submit -> when {
                gameConfig.numberOfTries <= 0 || gameConfig.wordLength <= 0 -> GameState.Invalid(IllegalArgumentException("Invalid game config"))
                guess.isEmpty() -> GameState.Start
                guess.length % gameConfig.wordLength != 0 -> GameState.Invalid(IllegalArgumentException("Attempts must be of length ${gameConfig.wordLength}"))
                guess.length / gameConfig.wordLength > gameConfig.numberOfTries -> GameState.Invalid(IllegalArgumentException("Too many attempts. Max attempts is ${gameConfig.numberOfTries}"))
                else -> {
                    val resultState = guess.chunked(gameConfig.wordLength).map { attempt -> wordMatcher.match(wordProvider.get(), attempt) }.flatten()
                    if (resultState.size < gameConfig.numberOfTries) GameState.Current(resultState)
                    else GameState.Finish(resultState)
                }
            }
        }
    }
}