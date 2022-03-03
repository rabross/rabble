package com.rabross.rabble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rabross.rabble.game.Game
import com.rabross.rabble.game.GameState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RabbleActivity : ComponentActivity() {

    @Inject
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Game(previewViewState)
        }
    }
}

typealias WordState = List<String>

data class ViewState(
    val words: WordState,
    val gameState: GameState
)

val emptyViewState = ViewState(listOf(), listOf(listOf()))
val previewViewState = ViewState(
    listOf("hello", "world"), listOf(
        listOf(2, 1, 0, 0, 2),
        listOf(0, 2, 1, 2, 2),
    )
)

@Composable
fun Game(state: ViewState = emptyViewState) {
    Column {
        for (wordIndex in state.words.indices) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (letterIndex in state.words[wordIndex].indices) {
                    val color = remember { getColour(state.gameState[wordIndex][letterIndex]) }
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f, true)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.words[wordIndex][letterIndex].uppercase())
                    }
                }
            }
        }
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