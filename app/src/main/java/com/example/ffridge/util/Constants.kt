package com.example.ffridge.util

object Constants {

    // App Constants
    const val APP_NAME = "ffridge"
    const val APP_VERSION = "1.0.0"

    // Database
    const val DATABASE_NAME = "ffridge_database"
    const val DATABASE_VERSION = 1

    // DataStore
    const val PREFERENCES_NAME = "user_preferences"

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm"

    // Expiry Thresholds (in days)
    const val EXPIRY_WARNING_DAYS = 3
    const val EXPIRY_CRITICAL_DAYS = 1

    // Chat
    const val MAX_CHAT_HISTORY = 10
    const val MAX_MESSAGE_LENGTH = 500

    // Recipe
    const val MIN_INGREDIENTS_FOR_RECIPE = 2
    const val MAX_RECIPE_COOKING_TIME = 300 // minutes

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "ffridge_expiry_channel"
    const val NOTIFICATION_ID = 1001

    // Units
    val COMMON_UNITS = listOf(
        "pcs", "kg", "g", "L", "ml", "tbsp", "tsp", "cup", "oz", "lb"
    )

    // Categories with emojis
    val CATEGORY_ICONS = mapOf(
        "DAIRY" to "🥛",
        "MEAT" to "🥩",
        "PANTRY" to "📦",
        "FROZEN" to "❄️",
        "VEGETABLES" to "🥬",
        "FRUITS" to "🍎",
        "OTHER" to "⭕"
    )
}
