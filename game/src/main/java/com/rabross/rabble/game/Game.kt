package com.rabross.rabble.game

interface GameConfig {
    val numberOfTries: Int get() = 1
    val wordLength: Int get() = 1
}

typealias MatchState = List<List<Int>>

interface Game {

    sealed class State {
        object Start: State()
        data class Current(val match: MatchState): State()
        data class Finish(val match: MatchState): State()
        data class Invalid(val reason: Exception): State()
    }

    suspend fun state(state: List<String>): State
}

class GameImpl(private val wordProvider: WordProvider, private val wordMatcher: WordMatcher, private val gameConfig: GameConfig) : Game {
    override suspend fun state(state: List<String>): Game.State {
        return try {
            when {
                state.isEmpty() -> Game.State.Start
                state.any { it.length != gameConfig.wordLength} -> Game.State.Invalid(IllegalArgumentException("Attempts must be of length ${gameConfig.wordLength}"))
                state.size > gameConfig.numberOfTries -> Game.State.Invalid(IllegalArgumentException("Too many attempts. Max attempts is ${gameConfig.numberOfTries}"))
                else -> {
                    val resultState = state.map { attempt -> wordMatcher.match(wordProvider.get(), attempt) }
                    if(resultState.size < gameConfig.numberOfTries) Game.State.Current(resultState)
                    else Game.State.Finish(resultState)
                }
            }
        } catch (e: IllegalArgumentException){
            Game.State.Invalid(e)
        }
    }
}