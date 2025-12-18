package com.example.ffridge.domain.usecase

import com.example.ffridge.data.model.Ingredient
import com.example.ffridge.data.repository.IngredientRepository

class AddIngredientUseCase(
    private val repository: IngredientRepository
) {
    suspend operator fun invoke(ingredient: Ingredient): Result<Unit> {
        return try {
            validateIngredient(ingredient)
            repository.insertIngredient(ingredient)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateIngredient(ingredient: Ingredient) {
        require(ingredient.name.isNotBlank()) { "Ingredient name cannot be empty" }
        require(ingredient.quantity.isNotBlank()) { "Quantity cannot be empty" }
        require(ingredient.unit.isNotBlank()) { "Unit cannot be empty" }
    }
}
