package com.rabross.rabble

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RabbleActivity : ComponentActivity() {

    private val viewModel: RabbleViewModelImpl by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                val state = viewModel.viewState.collectAsState()
                val coroutineScope = rememberCoroutineScope()
                PlayArea(state = state.value,
                    onTextChange = { text ->
                        //todo check if new text is valid
                        // i.e. disregard extra letters at the end of a line
                        coroutineScope.launch { viewModel.textChanged(text) }
                    },
                    onSubmit = { text ->
                        coroutineScope.launch { viewModel.submit() }
                    })
            }
        }
    }
}