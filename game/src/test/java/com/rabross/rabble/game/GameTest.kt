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
        val expected = Game.State.Start

        val sut = GameImpl(mock(), mock(), mock())
        val actual = sut.state(emptyList())

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
        val expected = Game.State.Finish(listOf(listOf(1)))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(listOf("a"))

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
        val expected = Game.State.Current(listOf(listOf(1)))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(listOf("a"))

        assertEquals(expected, actual)
    }

    @Test
    fun `given word matcher throws error, state is invalid`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doThrow IllegalArgumentException() }

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mock())
        val actual = sut.state(listOf("a"))

        assertThat(actual, `is`(instanceOf(Game.State.Invalid::class.java)))
        actual as Game.State.Invalid
        assertThat(actual.reason, `is`(instanceOf(IllegalArgumentException::class.java)))
    }

    @Test
    fun `given invalid word length state, state is invalid`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "a" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val mockGameConfig = mock<GameConfig> {
            on { numberOfTries } doReturn 1
            on { wordLength } doReturn 1
        }
        val expected = Game.State.Invalid(IllegalArgumentException("Attempts must be of length 1"))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(listOf("big"))

        assertThat(actual, `is`(instanceOf(Game.State.Invalid::class.java)))
        actual as Game.State.Invalid
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
        val expected = Game.State.Invalid(IllegalArgumentException("Too many attempts. Max attempts is 1"))

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mockGameConfig)
        val actual = sut.state(listOf("a", "b"))

        assertThat(actual, `is`(instanceOf(Game.State.Invalid::class.java)))
        actual as Game.State.Invalid
        assertExceptionEqual(expected.reason, actual.reason)
    }

    @Test
    fun `multiple attempts`() = runTest {
        val mockWordProvider = mock<WordProvider> { onBlocking { get() } doReturn "a" }
        val mockWordMatcher = mock<WordMatcher> { onBlocking { match(any(), any()) } doReturn listOf(1) }
        val expected = Game.State.Start

        val sut = GameImpl(mockWordProvider, mockWordMatcher, mock())
        val actual = sut.state(emptyList())

        assertEquals(expected, actual)
    }

    private fun assertExceptionEqual(expected: Exception, actual: Exception){
        assertThat(actual, `is`(instanceOf(IllegalArgumentException::class.java)))
        assertEquals(expected.message, actual.message)
    }
}