package com.example.ffridge.data.local.dao

import androidx.room.*
import com.example.ffridge.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Query("SELECT * FROM ingredients ORDER BY addedDate DESC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE id = :id")
    suspend fun getIngredientById(id: String): IngredientEntity?

    @Query("SELECT * FROM ingredients WHERE category = :category ORDER BY addedDate DESC")
    fun getIngredientsByCategory(category: String): Flow<List<IngredientEntity>>

    @Query("""
        SELECT * FROM ingredients 
        WHERE expiryDate IS NOT NULL 
        AND expiryDate <= :timestamp 
        AND expiryDate >= :currentTime
        ORDER BY expiryDate ASC
    """)
    fun getExpiringIngredients(timestamp: Long, currentTime: Long): Flow<List<IngredientEntity>>

    @Query("""
        SELECT * FROM ingredients 
        WHERE name LIKE '%' || :searchQuery || '%' 
        ORDER BY addedDate DESC
    """)
    fun searchIngredients(searchQuery: String): Flow<List<IngredientEntity>>

    @Query("SELECT COUNT(*) FROM ingredients")
    fun getIngredientCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity)

    @Delete
    suspend fun deleteIngredient(ingredient: IngredientEntity)

    @Query("DELETE FROM ingredients WHERE id = :id")
    suspend fun deleteIngredientById(id: String)

    @Query("DELETE FROM ingredients")
    suspend fun deleteAllIngredients()

    @Query("""
        SELECT * FROM ingredients 
        WHERE expiryDate IS NOT NULL 
        AND expiryDate < :currentTime
    """)
    fun getExpiredIngredients(currentTime: Long): Flow<List<IngredientEntity>>
}
