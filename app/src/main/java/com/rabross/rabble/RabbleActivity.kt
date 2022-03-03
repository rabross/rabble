package com.rabross.rabble

import androidx.activity.ComponentActivity
import com.rabross.rabble.game.Game
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RabbleActivity: ComponentActivity() {

    @Inject lateinit var game: Game

}