package com.example.ffridge.domain.usecase

import com.example.ffridge.data.model.Ingredient
import com.example.ffridge.data.repository.IngredientRepository

class UpdateIngredientUseCase(
    private val repository: IngredientRepository
) {
    suspend operator fun invoke(ingredient: Ingredient): Result<Unit> {
        return try {
            repository.updateIngredient(ingredient)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
