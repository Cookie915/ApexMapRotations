package com.example.apexmaprotations

import com.example.apexmaprotations.util.capitalizeWords
import org.junit.Assert
import org.junit.Test

class StringCapitalizeExtensionTest {
    @Test
    fun `test capitalize a sentence`() = kotlin.run {
        Assert.assertEquals("This Is A Test Sentence", "this is a test sentence".capitalizeWords())
    }

    @Test
    fun `test capitalize single word`() = kotlin.run {
        Assert.assertEquals("Word", "word".capitalizeWords())
    }

    @Test
    fun `capitalize a collection`() = kotlin.run {
        val wordList = listOf("capitalize", "this", "word", "collection")
        val capitalizedWordList = wordList.map { it.capitalizeWords() }
        Assert.assertEquals(listOf("Capitalize", "This", "Word", "Collection"), capitalizedWordList)
    }

}