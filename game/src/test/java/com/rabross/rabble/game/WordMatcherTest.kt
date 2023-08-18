package com.rabross.rabble.game

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
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
    fun `given mismatching words, match returns full absent`() = runTest {
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
    fun `duplicate letters in attempt with correct when duplicate is first`() = runTest {
        val sut = WordMatcherImpl()
        val word = "sam"
        val attempt = "sss"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_ABSENT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in attempt with correct when duplicate is second`() = runTest {
        val sut = WordMatcherImpl()
        val word = "sam"
        val attempt = "aaa"
        val expected = listOf(WordMatcher.TOKEN_ABSENT, WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in attempt no correct`() = runTest {
        val sut = WordMatcherImpl()
        val word = "sam"
        val attempt = "ass"
        val expected = listOf(WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in word and attempt same positions`() = runTest {
        val sut = WordMatcherImpl()
        val word = "ssa"
        val attempt = "ssm"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_ABSENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in word and attempt different positions`() = runTest {
        val sut = WordMatcherImpl()
        val word = "ssa"
        val attempt = "sas"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate letters in word and attempt but no clash`() = runTest {
        val sut = WordMatcherImpl()
        val word = "ssam"
        val attempt = "mass"
        val expected = listOf(WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT, WordMatcher.TOKEN_PRESENT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `duplicate separate but not first letter`() = runTest {
        val sut = WordMatcherImpl()
        val word = "sama"
        val attempt = "aaaa"
        val expected = listOf(WordMatcher.TOKEN_ABSENT, WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_ABSENT, WordMatcher.TOKEN_CORRECT)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `Unequal sizes adapts to solution size and return TOKEN_EMPTY`() = runTest {
        val sut = WordMatcherImpl()
        val word = "bigger"
        val attempt = "big"
        val expected = listOf(WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_CORRECT, WordMatcher.TOKEN_EMPTY, WordMatcher.TOKEN_EMPTY, WordMatcher.TOKEN_EMPTY)
        val actual = sut.match(word, attempt)
        Assert.assertEquals(expected, actual)
    }
}