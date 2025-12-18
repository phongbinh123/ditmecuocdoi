package com.example.ffridge.data.local.database

import com.example.ffridge.data.local.entity.ChatMessageEntity
import com.example.ffridge.data.local.entity.IngredientEntity
import com.example.ffridge.data.local.entity.RecipeEntity
import java.util.UUID

object DatabaseTestHelper {

    fun createSampleIngredient(
        name: String = "Test Ingredient",
        category: String = "OTHER",
        expiryDays: Int = 7
    ): IngredientEntity {
        return IngredientEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            quantity = "1",
            unit = "kg",
            category = category,
            expiryDate = System.currentTimeMillis() + (expiryDays * 24 * 60 * 60 * 1000),
            addedDate = System.currentTimeMillis(),
            notes = null,
            imageUrl = null
        )
    }

    fun createSampleRecipe(
        title: String = "Test Recipe",
        difficulty: String = "EASY"
    ): RecipeEntity {
        return RecipeEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = "A delicious test recipe",
            ingredients = listOf("Ingredient 1", "Ingredient 2", "Ingredient 3"),
            instructions = listOf("Step 1", "Step 2", "Step 3"),
            cookingTime = 30,
            difficulty = difficulty,
            imageUrl = null,
            createdAt = System.currentTimeMillis(),
            isFavorite = false
        )
    }

    fun createSampleChatMessage(
        role: String = "USER",
        text: String = "Test message"
    ): ChatMessageEntity {
        return ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            role = role,
            text = text,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun clearAllData(database: FfridgeDatabase) {
        database.ingredientDao().deleteAllIngredients()
        database.recipeDao().deleteAllRecipes()
        database.chatMessageDao().deleteAllMessages()
    }
}
