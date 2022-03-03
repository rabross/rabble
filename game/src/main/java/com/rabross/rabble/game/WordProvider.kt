package com.rabross.rabble.game

interface WordProvider {
    suspend fun get(): String
}