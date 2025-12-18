package com.example.ffridge.data.repository

import android.content.Context
import com.example.ffridge.data.local.database.DatabaseProvider

object RepositoryProvider {

    private var ingredientRepository: IngredientRepository? = null
    private var recipeRepository: RecipeRepository? = null
    private var chatRepository: ChatRepository? = null
    private var userRepository: UserRepository? = null

    fun initialize(context: Context) {
        if (ingredientRepository == null) {
            ingredientRepository = IngredientRepository(DatabaseProvider.getIngredientDao())
        }
        if (recipeRepository == null) {
            recipeRepository = RecipeRepository(DatabaseProvider.getRecipeDao())
        }
        if (chatRepository == null) {
            chatRepository = ChatRepository(DatabaseProvider.getChatMessageDao())
        }
        if (userRepository == null) {
            userRepository = UserRepository(context.applicationContext)
        }
    }

    fun getIngredientRepository(): IngredientRepository {
        return ingredientRepository ?: throw IllegalStateException(
            "RepositoryProvider not initialized. Call initialize(context) first."
        )
    }

    fun getRecipeRepository(): RecipeRepository {
        return recipeRepository ?: throw IllegalStateException(
            "RepositoryProvider not initialized. Call initialize(context) first."
        )
    }

    fun getChatRepository(): ChatRepository {
        return chatRepository ?: throw IllegalStateException(
            "RepositoryProvider not initialized. Call initialize(context) first."
        )
    }

    fun getUserRepository(): UserRepository {
        return userRepository ?: throw IllegalStateException(
            "RepositoryProvider not initialized. Call initialize(context) first."
        )
    }
}
