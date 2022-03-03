package com.rabross.rabble.game

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.*

class WordMatcherTest {

    @Test
    fun `given matching words, match returns full correct`() = runTest {
        val sut = WordMatcherImpl()
        val word = "eli"
        val expected = Collections.nCopies(word.length, WordMatcher.TOKEN_CORRECT);
        val actual = sut.match(word, word)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given missmatching words, match returns full absent`() = runTest {
        val sut = WordMatcherImpl()
        val word = "eli"
        val attempt = "sam"
        val expected = Collections.nCopies(word.length, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given words with matching present letters, match returns full present`() = runTest {
        val sut = WordMatcherImpl()
        val word = "eli"
        val attempt = "lie"
        val expected = Collections.nCopies(word.length, WordMatcher.TOKEN_PRESENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given words correct, present and absent letters, match returns full present`() = runTest {
        val sut = WordMatcherImpl()
        val word = "sam"
        val attempt = "smx"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in word`() = runTest {
        val sut = WordMatcherImpl()
        val word = "ssa"
        val attempt = "sam"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in answer`() = runTest {
        val sut = WordMatcherImpl()
        val word = "sam"
        val attempt = "ssa"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in word and answer same positions`() = runTest {
        val sut = WordMatcherImpl()
        val word = "ssa"
        val attempt = "ssm"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in word and answer different positions`() = runTest {
        val sut = WordMatcherImpl()
        val word = "ssa"
        val attempt = "sas"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Unequal sizes throws exception`() = runTest {
        val sut = WordMatcherImpl()
        val word = "small"
        val attempt = "big"
        sut.match(word, attempt)
    }
}