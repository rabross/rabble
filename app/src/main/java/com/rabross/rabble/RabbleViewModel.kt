package com.rabross.rabble

import androidx.lifecycle.ViewModel
import com.rabross.rabble.game.Game
import com.rabross.rabble.game.GameConfig
import com.rabross.rabble.game.GameState
import com.rabross.rabble.game.PlayState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RabbleViewModelImpl @Inject constructor(
    private val game: Game,
    private val gameConfig: GameConfig
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        ViewState(
            gameConfig.numberOfTries,
            gameConfig.wordLength,
            emptyList(),
            GameState.Start
        )
    )

    val viewState = _viewState.asStateFlow()

    private val letters get() = viewState.value.words.joinToString()

    var turnCount = 0
        private set

    suspend fun textChanged(text: String) {
        _viewState.value = ViewState(
            gameConfig.numberOfTries,
            gameConfig.wordLength,
            text.chunked(gameConfig.wordLength),
            game.state(PlayState.Typing(text))
        )
    }

    suspend fun submit() {
        _viewState.value = ViewState(
            gameConfig.numberOfTries,
            gameConfig.wordLength,
            letters.chunked(gameConfig.wordLength),
            game.state(PlayState.Submit(letters))
        )
    }

    suspend fun backspace() {
        textChanged(letters.dropLast(1))
    }

    data class ViewState(
        val turns: Int,
        val wordLength: Int,
        val words: WordState,
        val gameState: GameState
    )
}