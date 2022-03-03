package com.rabross.rabble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.rabross.rabble.game.Game
import com.rabross.rabble.game.GameConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
                Game.State.Start
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                val state by remember { viewState }
                PlayArea(state = state) { _ ->
                    viewState.value = viewState.value.copy()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            delay(2000)
            viewState.value = ViewState(
                gameConfig.numberOfTries,
                gameConfig.wordLength,
                listOf("hello"),
                game.state(listOf("hello"))
            )
            delay(2000)
            viewState.value = ViewState(
                gameConfig.numberOfTries,
                gameConfig.wordLength,
                listOf("hello", "world"),
                game.state(listOf("hello", "world"))
            )
            delay(2000)
            viewState.value = ViewState(
                gameConfig.numberOfTries,
                gameConfig.wordLength,
                listOf("hello", "world", "rabbl"),
                game.state(listOf("hello", "world", "rabbl"))
            )
        }
    }
}

typealias WordState = List<String>

data class ViewState(
    val turns: Int,
    val wordLength: Int,
    val words: WordState,
    val gameState: Game.State
)