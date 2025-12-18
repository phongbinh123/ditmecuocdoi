package com.example.ffridge.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.ffridge.data.model.Ingredient
import java.text.SimpleDateFormat
import java.util.*

/**
 * String Extensions
 */
fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

fun String.toTitleCase(): String {
    return this.lowercase().split(" ").joinToString(" ") { it.capitalize() }
}

/**
 * List Extensions
 */
fun <T> List<T>.second(): T? {
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.third(): T? {
    return if (this.size >= 3) this[2] else null
}

/**
 * Ingredient Extensions
 */
fun Ingredient.isExpired(): Boolean {
    return expiryDate?.let { it < System.currentTimeMillis() } ?: false
}

fun Ingredient.isExpiringSoon(days: Int = 3): Boolean {
    return expiryDate?.let {
        val daysUntil = DateUtils.getDaysUntil(it)
        daysUntil in 0..days
    } ?: false
}

fun Ingredient.getDaysUntilExpiry(): Int? {
    return expiryDate?.let { DateUtils.getDaysUntil(it) }
}

fun Ingredient.getExpiryStatusText(): String {
    val days = getDaysUntilExpiry() ?: return "No expiry date"

    return when {
        days < 0 -> "Expired ${Math.abs(days)} days ago"
        days == 0 -> "Expires today"
        days == 1 -> "Expires tomorrow"
        else -> "Expires in $days days"
    }
}

/**
 * Compose Extensions
 */
@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

@Composable
fun Int.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

/**
 * Number Extensions
 */
fun Float.format(decimals: Int = 2): String {
    return "%.${decimals}f".format(this)
}

fun Double.format(decimals: Int = 2): String {
    return "%.${decimals}f".format(this)
}

/**
 * Collection Extensions
 */
fun <T> List<T>.randomOrNull(): T? {
    return if (this.isNotEmpty()) this.random() else null
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}
