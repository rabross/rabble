package com.rabross.rabble

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rabross.rabble.game.Game

val previewViewState = ViewState(
    6, 5,
    listOf("hello", "world", "rabbl"), Game.State.Current(
        listOf(
            0, 0, 1, 0, 0,
            0, 0, 1, 1, 0,
            2, 2, 2, 2, 2,
        )
    )
)

@Preview(name = "game")
@Composable
fun GamePreview() {
    Surface(color = Color.White) {
        PlayArea(state = previewViewState, onTextChange = { /* noop */ })
    }
}

@Composable
fun PlayArea(
    modifier: Modifier = Modifier.padding(12.dp),
    state: ViewState,
    onTextChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        GameBoard(modifier = modifier, state = state)
        Keyboard(modifier = modifier, state = state, onTextChange = onTextChange)
    }
}

@Composable
fun Keyboard(modifier: Modifier, state: ViewState, onTextChange: (String) -> Unit) {
    Box(modifier = modifier.fillMaxWidth()) {
        val text = remember { mutableStateOf("") }
        val onKeyClick: (Char) -> Unit = { letter ->
            text.value = text.value + letter
            onTextChange(text.value)
        }
        val onBackSpaceClick = {
            text.value = text.value.dropLast(1)
            onTextChange(text.value)
        }
        Column(verticalArrangement = Arrangement.Top) {
            KeyboardRow1(onKeyClick)
            KeyboardRow2(onKeyClick)
            KeyboardRow3(onKeyClick, onBackSpaceClick)
        }
    }
}

@Composable
private fun KeyboardRow1(onClick: (Char) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        val letters = "qwertyuiop"
        for (letter in letters) {
            Key(letter, onClick)
        }
    }
}

@Composable
private fun KeyboardRow2(onClick: (Char) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        val letters = "asdfghjkl"
        for (letter in letters) {
            Key(letter, onClick)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun KeyboardRow3(
    onClick: (Char) -> Unit,
    onBackSpaceClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        val letters = "zxcvbnm"
        val keyColor = Color(211, 214, 218)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(3.dp), backgroundColor = keyColor
        ) {
            BoxWithConstraints(contentAlignment = Alignment.Center) {
                Text(text = "MMMMM", color = Color(211, 214, 218))
                Text(text = "^", color = Color(94, 95, 97))
            }
        }
        for (letter in letters) {
            Key(letter, onClick)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(3.dp), backgroundColor = keyColor, onClick = onBackSpaceClick
        ) {
            BoxWithConstraints(contentAlignment = Alignment.Center) {
                Text(text = "MMMMM", color = Color(211, 214, 218))
                Text(text = "<", color = Color(94, 95, 97))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RowScope.Key(letter: Char, onClick: (Char) -> Unit = {}) {
    val keyColor = Color(211, 214, 218)
    Card(modifier = Modifier
        .aspectRatio(0.8f)
        .fillMaxWidth()
        .weight(1f)
        .padding(3.dp), backgroundColor = keyColor, onClick = { onClick(letter) }) {
        BoxWithConstraints(contentAlignment = Alignment.Center) {
            Text(text = "M", color = keyColor)
            Text(text = letter.uppercase(), color = Color(94, 95, 97))
        }
    }
}

@Composable
fun GameBoard(modifier: Modifier, state: ViewState) {
    Box(modifier = modifier) {
        EmptyTileGrid(state)
        Game(state)
    }
}

@Composable
fun Game(state: ViewState) {
    Column {
        for (wordIndex in state.words.indices) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (letterIndex in state.words[wordIndex].indices) {
                    val letter = state.words[wordIndex][letterIndex]
                    val matchIndex = state.wordLength * wordIndex + letterIndex
                    when (state.gameState) {
                        is Game.State.Current -> LetterTile(
                            letter = letter,
                            state = state.gameState.match[matchIndex]
                        )
                        is Game.State.Finish -> LetterTile(
                            letter = letter,
                            state = state.gameState.match[matchIndex]
                        )
                        is Game.State.Invalid -> { /*noop*/
                        }
                        Game.State.Start -> { /*noop*/
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTileGrid(state: ViewState) {
    Column {
        repeat(state.turns) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(state.wordLength) {
                    EmptyTile()
                }
            }
        }
    }
}

@Composable
fun RowScope.LetterTile(letter: Char, state: Int) {
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
fun RowScope.EmptyTile() {
    Box(
        modifier = Modifier
            .weight(1f, true)
            .aspectRatio(1f)
            .padding(3.dp)
            .border(2.dp, Color(211, 214, 218))
    )
}

private fun getColour(state: Int): Color {
    return when (state) {
        2 -> Color(106, 170, 100)
        1 -> Color(201, 180, 88)
        else -> Color(120, 124, 125)
    }
}