package com.example.ffridge.domain.usecase

import com.example.ffridge.data.model.Ingredient
import com.example.ffridge.data.repository.IngredientRepository

class DeleteIngredientUseCase(
    private val repository: IngredientRepository
) {
    suspend operator fun invoke(ingredient: Ingredient): Result<Unit> {
        return try {
            repository.deleteIngredient(ingredient)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
