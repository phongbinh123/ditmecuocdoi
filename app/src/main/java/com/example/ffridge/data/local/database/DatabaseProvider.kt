package com.example.ffridge.data.local.database

import android.content.Context

object DatabaseProvider {

    private var database: FfridgeDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = FfridgeDatabase.getDatabase(context)
        }
    }

    fun getDatabase(): FfridgeDatabase {
        return database ?: throw IllegalStateException(
            "DatabaseProvider must be initialized before use. Call initialize(context) first."
        )
    }

    fun getIngredientDao() = getDatabase().ingredientDao()

    fun getRecipeDao() = getDatabase().recipeDao()

    fun getChatMessageDao() = getDatabase().chatMessageDao()
}
