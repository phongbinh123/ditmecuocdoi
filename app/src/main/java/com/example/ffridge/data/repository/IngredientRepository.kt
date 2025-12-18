package com.example.ffridge.data.repository

import com.example.ffridge.data.local.dao.IngredientDao
import com.example.ffridge.data.local.entity.IngredientEntity
import com.example.ffridge.data.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IngredientRepository(
    private val ingredientDao: IngredientDao
) : BaseRepository<Ingredient> {

    // Get all ingredients
    override fun getAll(): Flow<List<Ingredient>> {
        return ingredientDao.getAllIngredients().map { entities ->
            entities.map { it.toIngredient() }
        }
    }

    // Get ingredient by ID
    override suspend fun getById(id: String): Ingredient? {
        return ingredientDao.getIngredientById(id)?.toIngredient()
    }

    // Insert ingredient
    override suspend fun insert(item: Ingredient) {
        ingredientDao.insertIngredient(item.toEntity())
    }

    // Update ingredient
    override suspend fun update(item: Ingredient) {
        ingredientDao.updateIngredient(item.toEntity())
    }

    // Delete ingredient
    override suspend fun delete(item: Ingredient) {
        ingredientDao.deleteIngredient(item.toEntity())
    }

    // Delete ingredient by ID
    override suspend fun deleteById(id: String) {
        ingredientDao.deleteIngredientById(id)
    }

    // Get ingredients by category
    fun getByCategory(category: String): Flow<List<Ingredient>> {
        return ingredientDao.getIngredientsByCategory(category).map { entities ->
            entities.map { it.toIngredient() }
        }
    }

    // Get expiring ingredients (within specified days)
    fun getExpiringIngredients(daysAhead: Int = 3): Flow<List<Ingredient>> {
        val currentTime = System.currentTimeMillis()
        val futureTime = currentTime + (daysAhead * 24 * 60 * 60 * 1000L)
        return ingredientDao.getExpiringIngredients(futureTime, currentTime).map { entities ->
            entities.map { it.toIngredient() }
        }
    }

    // Search ingredients
    fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return ingredientDao.searchIngredients(query).map { entities ->
            entities.map { it.toIngredient() }
        }
    }

    // Get ingredient count
    suspend fun getCount(): Int {
        return ingredientDao.getIngredientCount()
    }

    // Get ingredient count by category
    suspend fun getCountByCategory(category: String): Int {
        return ingredientDao.getIngredientCountByCategory(category)
    }

    // Insert multiple ingredients
    suspend fun insertMultiple(ingredients: List<Ingredient>) {
        ingredientDao.insertIngredients(ingredients.map { it.toEntity() })
    }

    // Delete all ingredients
    suspend fun deleteAll() {
        ingredientDao.deleteAllIngredients()
    }

    // Delete expired ingredients
    suspend fun deleteExpired() {
        val currentTime = System.currentTimeMillis()
        ingredientDao.deleteExpiredIngredients(currentTime)
    }

    // Check if ingredient exists
    suspend fun exists(id: String): Boolean {
        return ingredientDao.getIngredientById(id) != null
    }

    // Get ingredients expiring today
    fun getExpiringToday(): Flow<List<Ingredient>> {
        val startOfDay = getStartOfDay()
        val endOfDay = getEndOfDay()
        return ingredientDao.getExpiringIngredients(endOfDay, startOfDay).map { entities ->
            entities.map { it.toIngredient() }
        }
    }

    // Mapper functions
    private fun IngredientEntity.toIngredient() = Ingredient(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit,
        category = category,
        expiryDate = expiryDate,
        addedDate = addedDate,
        notes = notes,
        imageUrl = imageUrl
    )

    private fun Ingredient.toEntity() = IngredientEntity(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit,
        category = category,
        expiryDate = expiryDate,
        addedDate = addedDate,
        notes = notes,
        imageUrl = imageUrl
    )

    // Utility functions
    private fun getStartOfDay(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}
