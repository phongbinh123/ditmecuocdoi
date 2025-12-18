package com.example.ffridge.data.local.database

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseHelper {

    /**
     * Clear all data from database
     */
    suspend fun clearAllData(context: Context) {
        withContext(Dispatchers.IO) {
            val database = FfridgeDatabase.getDatabase(context)
            database.ingredientDao().deleteAllIngredients()
            database.recipeDao().deleteAllRecipes()
            database.chatMessageDao().deleteAllMessages()
        }
    }

    /**
     * Clear expired ingredients
     */
    suspend fun clearExpiredIngredients(context: Context) {
        withContext(Dispatchers.IO) {
            val database = FfridgeDatabase.getDatabase(context)
            val currentTime = System.currentTimeMillis()
            database.ingredientDao().deleteExpiredIngredients(currentTime)
        }
    }

    /**
     * Clear old chat messages (older than specified days)
     */
    suspend fun clearOldChatMessages(context: Context, daysToKeep: Int = 30) {
        withContext(Dispatchers.IO) {
            val database = FfridgeDatabase.getDatabase(context)
            val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
            database.chatMessageDao().deleteOldMessages(cutoffTime)
        }
    }

    /**
     * Get database statistics
     */
    suspend fun getDatabaseStats(context: Context): DatabaseStats {
        return withContext(Dispatchers.IO) {
            val database = FfridgeDatabase.getDatabase(context)
            DatabaseStats(
                ingredientCount = database.ingredientDao().getIngredientCount(),
                recipeCount = database.recipeDao().getRecipeCount(),
                chatMessageCount = database.chatMessageDao().getMessageCount(),
                favoriteRecipeCount = database.recipeDao().getFavoriteRecipeCount()
            )
        }
    }

    /**
     * Export database to JSON (for backup)
     */
    suspend fun exportToJson(context: Context): String {
        return withContext(Dispatchers.IO) {
            val database = FfridgeDatabase.getDatabase(context)
            // Implementation for JSON export
            // Return JSON string
            "{}"  // Placeholder
        }
    }

    /**
     * Import database from JSON (for restore)
     */
    suspend fun importFromJson(context: Context, jsonData: String) {
        withContext(Dispatchers.IO) {
            // Implementation for JSON import
        }
    }
}

data class DatabaseStats(
    val ingredientCount: Int,
    val recipeCount: Int,
    val chatMessageCount: Int,
    val favoriteRecipeCount: Int
)
