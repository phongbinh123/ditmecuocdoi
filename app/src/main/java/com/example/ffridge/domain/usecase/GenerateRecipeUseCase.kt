package com.example.ffridge.domain.usecase

import com.example.ffridge.data.model.Ingredient
import com.example.ffridge.data.model.Recipe
import com.example.ffridge.data.remote.GeminiService

class GenerateRecipeUseCase(
    private val geminiService: GeminiService
) {
    suspend operator fun invoke(ingredients: List<Ingredient>): Result<Recipe> {
        return try {
            val ingredientNames = ingredients.map { it.name }
            val recipe = geminiService.generateRecipe(ingredientNames)
            Result.success(recipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
