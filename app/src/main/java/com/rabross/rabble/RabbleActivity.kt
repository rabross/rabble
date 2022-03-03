package com.rabross.rabble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.rabross.rabble.game.Game
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class RabbleActivity : ComponentActivity() {

    @Inject
    lateinit var game: Game

    private val viewState = mutableStateOf(ViewState(emptyList(), Game.State.Start))

    private val attempts = listOf("hello", "world", "rabbl")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by remember { viewState }
            Game(state)
        }

        lifecycleScope.launchWhenCreated {
            delay(2000)
            val gameState = game.state(attempts)
            viewState.value = ViewState(attempts, gameState)
        }
    }
}

typealias WordState = List<String>

data class ViewState(
    val words: WordState,
    val gameState: Game.State
)

val emptyViewState = ViewState(listOf(), Game.State.Start)
val previewViewState = ViewState(
    listOf("hello", "world", "rabbl"), Game.State.Current(
        listOf(
            listOf(0, 0, 1, 0, 0),
            listOf(0, 0, 1, 1, 0),
            listOf(2, 2, 2, 2, 2),
        )
    )
)

@Composable
fun Game(state: ViewState = emptyViewState) {
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
                        is Game.State.Invalid -> TODO()
                        Game.State.Start -> TODO()
                    }
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
        Text(text = letter.uppercase())
    }
}

private fun getColour(state: Int): Color {
    return when (state) {
        2 -> Color.Green
        1 -> Color.Yellow
        else -> Color.Gray
    }
}

@Preview
@Composable
fun GamePreview() {
    Surface(color = Color.White) {
        Game(previewViewState)
    }
}