package com.gilz688.collectionround

import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionRoundTest {
    private val sampleList = listOf(4.0001, 3.2345, 3.2321, 6.4523, 5.3453, 7.3422)
    private val sampleMap = mapOf(
        "apple" to 60.19012964572332,
        "banana" to 15.428802458406679,
        "cherry" to 24.381067895870007
    )
    private val sampleNegNumList = sampleList.map { it * -1 }
    private val sampleHugeNumList = sampleList.map { it * 100000 }
    private val sampleIntList = listOf(4, 3, 3, 6, 5, 7)

    @Test
    fun testDefaultRounder() {
        val expected = 400000.0
        assertEquals(expected, defaultRounder(400010.0, -3))
    }

    @Test
    fun basicDifferenceStrategy() {
        val expected = listOf(4.0, 3.24, 3.23, 6.45, 5.35, 7.34)
        assertEquals(expected, sampleList.safeRound(2, Strategy.DIFFERENCE))
    }

    @Test
    fun basicLargestStrategy() {
        val expected = listOf(4.0, 3.23, 3.23, 6.45, 5.35, 7.35)
        assertEquals(expected, sampleList.safeRound(2, Strategy.LARGEST))
    }

    @Test
    fun basicSmallestStrategy() {
        val expected = listOf(4.0, 3.24, 3.23, 6.45, 5.35, 7.34)
        assertEquals(expected, sampleList.safeRound(2, Strategy.SMALLEST))
    }

    @Test
    fun testMap() {
        val expected = mapOf(
            "apple" to 60.0,
            "banana" to 16.0,
            "cherry" to 24.0
        )
        assertEquals(expected, sampleMap.safeRound(0))
    }

    @Test
    fun testNegativeNumbers() {
        val expected = listOf(-4.0, -3.24, -3.23, -6.45, -5.35, -7.34)
        assertEquals(expected, sampleNegNumList.safeRound(2))
    }

    @Test
    fun testHugeNumbers() {
        val expected = listOf(400000.0, 324000.0, 323000.0, 645000.0, 535000.0, 734000.0)
        val actual = sampleHugeNumList.safeRound( -3)
        assertEquals(expected, actual)
    }

    @Test
    fun testOverRound() {
        val expected = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        assertEquals(expected, sampleList.safeRound(-3))
    }

    @Test
    fun testOverWithSum() {
        val expected = listOf(0.0, 0.0, 0.0, 10.0, 10.0, 10.0)
        assertEquals(sampleList.safeRound(-1), expected)
    }
}