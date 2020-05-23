package com.gilz688.collectionround

import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round


/**
 * The strategies that can be used in rounding up the rounding errors.
 *  <li>{@link #DIFFERENCE}</li>
 *  <li>{@link #SMALLEST}</li>
 *  <li>{@link #LARGEST}</li>
 **/
enum class Strategy {
    /*
     * Seeks to minimize the sum of the array of the differences between the original value and the rounded value of each item in the collection. It will adjust the items with the largest difference to preserve the sum. This is the default.
     */
    DIFFERENCE,

    /*
     * For any post rounding adjustments, sort the array by the smallest values to largest, adjust the smaller ones first.
     */
    SMALLEST,

    /*
     * For any post rounding adjustments, sort the array by the largest values to smallest and adjust those first.
     */
    LARGEST
}


/**
 * Rounds the values of a map while retaining their original summed value.
 * @param   places      the number of places each value in the map should be rounded to
 * @param   strategy    the strategy used in clean up the rounding errors
 * @param   rounder     the rounding function
 * @return              Map with rounded values
 * @see                 Strategy
 */
@JvmOverloads
fun <K> Map<K, Number>.safeRound(
    places: Int,
    strategy: Strategy = Strategy.DIFFERENCE,
    rounder: (Double, Int) -> Double = ::defaultRounder
): Map<K, Number> {
    return keys.zip(values.safeRound(places, strategy, rounder)).associate { (key, value) -> key to value }
}

/**
 * Rounds a collection of numbers while retaining their original summed value.
 * @param   places      the number of places each item in the collection should be rounded to
 * @param   strategy    the strategy used in clean up the rounding errors
 * @param   rounder     the rounding function
 * @return              List with rounded numbers
 * @see                 Strategy
 */
@JvmOverloads
fun Iterable<Number>.safeRound(
    places: Int,
    strategy: Strategy = Strategy.DIFFERENCE,
    rounder: (Double, Int) -> Double = ::defaultRounder
): Iterable<Number> {
    // define a sorting method for rounded differences
    val sorter = if (strategy == Strategy.DIFFERENCE) ::sortByDiff else ::sortByValue
    val defaultReverse = strategy != Strategy.SMALLEST

    // calculate original sum, rounded, then rounded local sum
    var local = this.mapIndexed { idx, value -> Item(idx, value) }.toMutableList()
    val originalSum = rounder(this.sum(), places)
    var localSum = 0.0
    local.forEach {
        it.round(places, rounder)
        localSum += it.value
    }
    localSum = rounder(localSum, places)

    // adjust values to adhere to original sum
    var increment: Double
    var reverse: Boolean
    var tweaks: Int
    while (localSum != originalSum) {
        val diff = rounder(originalSum - localSum, places)
        if (diff < 0.0) {
            increment = -1.0 * minIncrement(places)
            reverse = if (strategy == Strategy.DIFFERENCE) false
            else defaultReverse
        } else {
            increment = minIncrement(places)
            reverse = if (strategy == Strategy.DIFFERENCE) true else defaultReverse
        }
        tweaks = (abs(diff) / minIncrement(places)).toInt()
        local = sorter(local, reverse).toMutableList()

        for (ith in 0 until min(tweaks, local.size)) {
            local[ith].value += increment
            local[ith].round(places, rounder)
        }
        localSum = rounder(local.fold(0.0) { acc, item -> acc + item.value }, places)
    }

    return sortByOrder(local).map { it.value }
}

private fun sortByValue(items: List<Item>, reverse: Boolean = false): List<Item> =
    if (reverse) items.sortedByDescending { it.value }
    else items.sortedBy { it.value }

private fun sortByOrder(items: List<Item>): List<Item> = items.sortedBy { it.order }

private fun sortByDiff(items: List<Item>, reverse: Boolean = false): List<Item> =
    if (reverse) items.sortedByDescending { it.diff }
    else items.sortedBy { it.diff }

private fun minIncrement(places: Int): Double = 1.0 / 10.0.pow(places)

private fun Iterable<Number>.sum(): Double =
    this.fold(0.0) { acc, num ->
        acc + num.toDouble()
    }

fun defaultRounder(number: Double, places: Int): Double {
    var multiplier = 10.0.pow(places)
    return round(number * multiplier) / multiplier
}

private data class Item(var order: Int, val original: Number) {
    var value: Double = original.toDouble()

    val diff: Double
        get() = original.toDouble() - value

    fun round(places: Int, rounder: (Double, Int) -> Double) {
        value = rounder(value, places)
    }
}