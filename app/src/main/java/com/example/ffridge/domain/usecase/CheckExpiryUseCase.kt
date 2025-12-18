package com.example.ffridge.domain.usecase

import com.example.ffridge.data.model.Ingredient
import com.example.ffridge.data.repository.IngredientRepository
import com.example.ffridge.domain.model.ExpiryStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CheckExpiryUseCase(
    private val repository: IngredientRepository
) {
    fun getExpiringIngredients(daysAhead: Int = 3): Flow<List<Ingredient>> {
        return repository.getExpiringIngredients(daysAhead)
    }

    fun getExpiredIngredients(): Flow<List<Ingredient>> {
        return repository.getExpiredIngredients()
    }

    fun checkExpiryStatus(ingredient: Ingredient): ExpiryStatus {
        if (ingredient.expiryDate == null) {
            return ExpiryStatus.NoExpiry
        }

        val currentTime = System.currentTimeMillis()
        val daysUntilExpiry = ((ingredient.expiryDate - currentTime) / (24 * 60 * 60 * 1000)).toInt()

        return when {
            daysUntilExpiry < 0 -> ExpiryStatus.Expired(Math.abs(daysUntilExpiry))
            daysUntilExpiry == 0 -> ExpiryStatus.ExpiringToday
            daysUntilExpiry <= 3 -> ExpiryStatus.ExpiringSoon(daysUntilExpiry)
            daysUntilExpiry <= 7 -> ExpiryStatus.ExpiringThisWeek(daysUntilExpiry)
            else -> ExpiryStatus.Fresh(daysUntilExpiry)
        }
    }
}
