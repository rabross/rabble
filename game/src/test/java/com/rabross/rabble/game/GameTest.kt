package com.rabross.rabble.game

import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock

class GameTest {

    @Test
    fun `given empty attempts game state returns start state`() = runTest {
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 1
        }
        val expected = GameState.Start

        val sut = GameImpl(mock(), mock(), mockGameConfig)
        val actual = sut.state(PlayState.Submit(""))

        assertEquals(expected, actual)
    }

    @Test
    fun `given number of attempts meets game config, game state returns finish state`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "a" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 1
        }
        val expected = GameState.Finish(listOf(1))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(PlayState.Submit("a"))

        assertEquals(expected, actual)
    }

    @Test
    fun `given number of attempts is lower than game config, game state returns current state`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "a" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 2
            on { wordLength } doReturn 1
        }
        val expected = GameState.Current(listOf(1))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(PlayState.Submit("a"))

        assertEquals(expected, actual)
    }

    @Test
    fun `given invalid word length state, state is invalid`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "samuel" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 6
        }
        val expected = GameState.Invalid(IllegalArgumentException("Attempts must be of length 6"))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(PlayState.Submit("lisa"))

        assertThat(actual, `is`(instanceOf(GameState.Invalid::class.java)))
        actual as GameState.Invalid
        assertExceptionEqual(expected.reason, actual.reason)
    }

    @Test
    fun `given number of attempts is higher than game config, game state is invalid`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "a" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 1
        }
        val expected = GameState.Invalid(IllegalArgumentException("Too many attempts. Max attempts is 1"))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(PlayState.Submit("ab"))

        assertThat(actual, `is`(instanceOf(GameState.Invalid::class.java)))
        actual as GameState.Invalid
        assertExceptionEqual(expected.reason, actual.reason)
    }

    @Test
    fun `multiple attempts`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "a" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 1
        }
        val expected = GameState.Start

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(PlayState.Submit(""))

        assertEquals(expected, actual)
    }

    @Test
    fun `given invalid game config an error is thrown`() = runTest {
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 0
            on { wordLength } doReturn 0
        }
        val expected = GameState.Invalid(IllegalArgumentException("Invalid game config"))

        val sut = GameImpl(mock(), mock(), mockGameConfig)
        val actual = sut.state(PlayState.Submit(""))

        assertThat(actual, `is`(instanceOf(GameState.Invalid::class.java)))
        actual as GameState.Invalid
        assertExceptionEqual(expected.reason, actual.reason)
    }

    @Test
    fun `given typing play state game with no typing returns an empty current game state`() = runTest {
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 1
        }
        val expected = GameState.Current(listOf(-1))

        val sut = GameImpl(mock(), mock(), mockGameConfig)
        val actual = sut.state(PlayState.Typing(""))

        assertEquals(expected, actual)
    }

    @Test
    fun `given typing play state with typing game returns an empty current game state with match state length equal to game config word length`() = runTest {
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 3
            on { wordLength } doReturn 3
        }
        val expected = GameState.Current(listOf(-1, -1, -1))

        val sut = GameImpl(mock(), mock(), mockGameConfig)
        val actual = sut.state(PlayState.Typing("eliandsam"))

        assertEquals(expected, actual)
    }

    private fun assertExceptionEqual(expected: Exception, actual: Exception){
        assertThat(actual, `is`(instanceOf(IllegalArgumentException::class.java)))
        assertEquals(expected.message, actual.message)
    }
}