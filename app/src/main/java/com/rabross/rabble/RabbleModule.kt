package com.rabross.rabble

import com.rabross.rabble.game.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RabbleModule {

    @Provides
    fun provideGame(wordProvider: WordProvider, wordMatcher: WordMatcher, gameConfig: GameConfig): Game {
        return GameImpl(wordProvider, wordMatcher, gameConfig)
    }

    @Provides
    fun provideWordProvider() = object : WordProvider { override suspend fun get() = "rabbl" }

    @Provides
    fun provideWordMatcher(): WordMatcher = WordMatcherImpl()

    @Provides
    fun provideGameConfig() = object : GameConfig {
        override val numberOfTries = 6
        override val wordLength = 5
    }
}