package com.example.ffridge.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val displayFormat = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
    private val storageFormat = SimpleDateFormat(Constants.DATE_FORMAT_STORAGE, Locale.getDefault())

    /**
     * Format timestamp to display date string
     */
    fun formatDate(timestamp: Long): String {
        return displayFormat.format(Date(timestamp))
    }

    /**
     * Format timestamp to storage date string
     */
    fun formatDateForStorage(timestamp: Long): String {
        return storageFormat.format(Date(timestamp))
    }

    /**
     * Parse storage date string to timestamp
     */
    fun parseStorageDate(dateString: String): Long? {
        return try {
            storageFormat.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get days between two timestamps
     */
    fun getDaysBetween(startTime: Long, endTime: Long): Int {
        val diffMillis = endTime - startTime
        return TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
    }

    /**
     * Get days until timestamp from now
     */
    fun getDaysUntil(timestamp: Long): Int {
        return getDaysBetween(System.currentTimeMillis(), timestamp)
    }

    /**
     * Check if date is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Check if date is in the past
     */
    fun isPast(timestamp: Long): Boolean {
        return timestamp < System.currentTimeMillis()
    }

    /**
     * Get relative time string (e.g., "2 days ago", "in 3 days")
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = timestamp - now
        val days = TimeUnit.MILLISECONDS.toDays(Math.abs(diff)).toInt()

        return when {
            diff < 0 -> {
                when (days) {
                    0 -> "Today"
                    1 -> "Yesterday"
                    else -> "$days days ago"
                }
            }
            else -> {
                when (days) {
                    0 -> "Today"
                    1 -> "Tomorrow"
                    else -> "In $days days"
                }
            }
        }
    }

    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    /**
     * Add days to timestamp
     */
    fun addDays(timestamp: Long, days: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis
    }
}
