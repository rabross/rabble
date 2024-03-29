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
import androidx.compose.ui.unit.sp
import com.rabross.rabble.RabbleViewModelImpl.ViewState
import com.rabross.rabble.game.GameState

val previewViewState = ViewState(
    6, 5,
    listOf("hello", "world", "type", "rabbl"), GameState.Current(
        listOf(
            0, 0, 1, 0, 0,
            0, 0, 1, 1, 0,
            -1,-1,-1,-1,-1,
            2, 2, 2, 2, 2,
        )
    )
)

@Preview(name = "game")
@Composable
fun GamePreview() {
    Surface(color = Color.White) {
        PlayArea(state = previewViewState, onTextChange = {}, onSubmit = {})
    }
}

@Composable
fun PlayArea(
    modifier: Modifier = Modifier.padding(12.dp),
    state: ViewState,
    onTextChange: (String) -> Unit,
    onSubmit: (String) -> Unit
) {
    Column(modifier = modifier) {
        GameBoard(modifier = modifier, state = state)
        Keyboard(modifier = modifier, state = state, onTextChange = onTextChange, onSubmit = onSubmit)
    }
}

@Composable
fun Keyboard(
    modifier: Modifier,
    state: ViewState,
    onTextChange: (String) -> Unit,
    onSubmit: (String) -> Unit
) {
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
        val onSubmitClick = {
            onSubmit(text.value)
        }
        Column(verticalArrangement = Arrangement.Top) {
            KeyboardRow1(onKeyClick)
            KeyboardRow2(onKeyClick)
            KeyboardRow3(onKeyClick, onBackSpaceClick, onSubmitClick)
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
    onBackSpaceClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        val letters = "zxcvbnm"
        val keyColor = Color(211, 214, 218)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(3.dp), backgroundColor = keyColor, onClick = onSubmitClick
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
                        is GameState.Current -> LetterTile(
                            letter = letter,
                            state = state.gameState.match[matchIndex]
                        )
                        is GameState.Finish -> LetterTile(
                            letter = letter,
                            state = state.gameState.match[matchIndex]
                        )
                        is GameState.Invalid -> { /*noop*/ }
                        GameState.Start -> { /*noop*/ }
                    }
                }
                repeat(state.wordLength - state.words[wordIndex].length){
                    EmptyTile()
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
    if(state == -1){
        UnmatchedTile(letter = letter)
    } else {
        val color = remember { getColour(state) }
        Box(
            modifier = Modifier
                .weight(1f, true)
                .aspectRatio(1f)
                .padding(3.dp)
                .background(color)
                .border(3.dp, color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = letter.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
    }
}

@Composable
fun RowScope.UnmatchedTile(letter: Char) {
    Box(
        modifier = Modifier
            .weight(1f, true)
            .aspectRatio(1f)
            .padding(3.dp)
            .border(3.dp, colorBorderUnmatched),
        contentAlignment = Alignment.Center
    ) {
        Text(text = letter.uppercase(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}

@Composable
fun RowScope.EmptyTile() {
    Box(
        modifier = Modifier
            .weight(1f, true)
            .aspectRatio(1f)
            .padding(3.dp)
            .border(3.dp, colorBorderEmpty)
    )
}

private fun getColour(state: Int): Color {
    return when (state) {
        2 -> colorMatch
        1 -> colorPresent
        0 -> colorAbsent
        else -> Color.Transparent
    }
}

private val colorMatch = Color(106, 170, 100)
private val colorPresent = Color(201, 180, 88)
private val colorAbsent = Color(120, 124, 126)
private val colorBorderUnmatched = Color(135, 138, 140)
private val colorBorderEmpty = Color(211, 214, 218)
