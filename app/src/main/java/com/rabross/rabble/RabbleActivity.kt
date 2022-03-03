package com.rabross.rabble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    private val viewState by lazy { mutableStateOf(ViewState(gameConfig.numberOfTries, gameConfig.numberOfTries, emptyList(), Game.State.Start)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by remember { viewState }
            GameGrid(gameConfig.numberOfTries, gameConfig.wordLength)
            Game(state)
        }

        lifecycleScope.launchWhenCreated {
            delay(2000)
            viewState.value = ViewState(gameConfig.numberOfTries, gameConfig.wordLength, listOf("hello"), game.state(listOf("hello")))
            delay(2000)
            viewState.value = ViewState(gameConfig.numberOfTries, gameConfig.wordLength, listOf("hello", "world"), game.state(listOf("hello", "world")))
            delay(2000)
            viewState.value = ViewState(gameConfig.numberOfTries, gameConfig.wordLength, listOf("hello", "world", "rabbl"), game.state(listOf("hello", "world", "rabbl")))
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

@Composable
fun Game(state: ViewState) {
    Column {
        for (wordIndex in state.words.indices) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (letterIndex in state.words[wordIndex].indices) {
                    when (state.gameState) {
                        is Game.State.Current -> Letter(
                            letter = state.words[wordIndex][letterIndex],
                            state = state.gameState.match[wordIndex][letterIndex]
                        )
                        is Game.State.Finish -> Letter(
                            letter = state.words[wordIndex][letterIndex],
                            state = state.gameState.match[wordIndex][letterIndex]
                        )
                        is Game.State.Invalid -> { /*noop*/ }
                        Game.State.Start -> { /*noop*/ }
                    }
                }
            }
        }
    }
}

@Composable
fun GameGrid(tries: Int, wordCount: Int) {
    Column {
        repeat(tries) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(wordCount) {
                    Cell()
                }
            }
        }
    }
}

@Composable
fun RowScope.Letter(letter: Char, state: Int) {
    val color = remember { getColour(state) }
    BoxWithConstraints(
        modifier = Modifier
            .weight(1f, true)
            .aspectRatio(1f)
            .padding(4.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = letter.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RowScope.Cell() {
    Box(
        modifier = Modifier
            .weight(1f, true)
            .aspectRatio(1f)
            .padding(4.dp)
            .border(2.dp, Color.LightGray)
    )
}

private fun getColour(state: Int): Color {
    return when (state) {
        2 -> Color(106, 170, 100)
        1 -> Color(202, 181, 87)
        else -> Color(121, 125, 126)
    }
}

val previewViewState = ViewState(
    6, 5,
    listOf("hello", "world", "rabbl"), Game.State.Current(
        listOf(
            listOf(0, 0, 1, 0, 0),
            listOf(0, 0, 1, 1, 0),
            listOf(2, 2, 2, 2, 2),
        )
    )
)

@Preview(name = "game")
@Composable
fun GamePreview() {
    Surface(color = Color.White) {
        GameGrid(previewViewState.turns,  previewViewState.wordLength)
        Game(previewViewState)
    }
}