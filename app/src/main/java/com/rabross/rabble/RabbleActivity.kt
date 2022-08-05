package com.rabross.rabble

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rabross.rabble.game.Game
import com.rabross.rabble.game.GameConfig
import com.rabross.rabble.game.GameState
import com.rabross.rabble.game.PlayState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RabbleActivity : ComponentActivity() {

    @Inject
    lateinit var game: Game

    @Inject
    lateinit var gameConfig: GameConfig

    private val viewState by lazy {
        mutableStateOf(
            ViewState(
                gameConfig.numberOfTries,
                gameConfig.wordLength,
                emptyList(),
                PlayState.Typing(""),
                GameState.Start
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                val state by remember { viewState }
                val coroutineScope = rememberCoroutineScope()
                PlayArea(state = state, onTextChange = { text ->
                    coroutineScope.launch {
                        val playState = PlayState.Typing(text)
                        val viewState1 = ViewState(
                            gameConfig.numberOfTries,
                            gameConfig.wordLength,
                            text.chunkedEqualised(gameConfig.wordLength),
                            playState,
                            game.state(playState)
                        )
                        viewState.value = viewState1
                    }
                }, onSubmit = { text ->
                    coroutineScope.launch {
                        val playState = PlayState.Submit(text)
                        val viewState1 = ViewState(
                            gameConfig.numberOfTries,
                            gameConfig.wordLength,
                            text.chunkedEqualised(gameConfig.wordLength),
                            playState,
                            game.state(playState)
                        )
                        viewState.value = viewState1
                    }
                })
            }
        }
    }
}

private fun String.chunkedEqualised(wordLength: Int) =
    chunked(wordLength) { text -> text.padEnd(wordLength, Char(32)).toString() }

typealias WordState = List<String>

data class ViewState(
    val turns: Int,
    val wordLength: Int,
    val words: WordState,
    val playState: PlayState,
    val gameState: GameState


) {
    override fun equals(other: Any?): Boolean {
        return false
    }
}