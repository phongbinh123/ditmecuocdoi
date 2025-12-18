package com.example.ffridge.domain.usecase

import com.example.ffridge.data.model.Recipe
import com.example.ffridge.data.model.RecipeDifficulty
import com.example.ffridge.data.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.getAllRecipes()
    }

    fun getFavorites(): Flow<List<Recipe>> {
        return repository.getFavoriteRecipes()
    }

    fun getByDifficulty(difficulty: RecipeDifficulty): Flow<List<Recipe>> {
        return repository.getRecipesByDifficulty(difficulty)
    }

    fun getQuickRecipes(): Flow<List<Recipe>> {
        return repository.getQuickRecipes()
    }

    fun search(query: String): Flow<List<Recipe>> {
        return repository.searchRecipes(query)
    }
}
