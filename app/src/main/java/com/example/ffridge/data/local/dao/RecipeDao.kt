package com.example.ffridge.data.local.dao

import androidx.room.*
import com.example.ffridge.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE difficulty = :difficulty ORDER BY createdAt DESC")
    fun getRecipesByDifficulty(difficulty: String): Flow<List<RecipeEntity>>

    @Query("""
        SELECT * FROM recipes 
        WHERE title LIKE '%' || :searchQuery || '%' 
        OR description LIKE '%' || :searchQuery || '%'
        ORDER BY createdAt DESC
    """)
    fun searchRecipes(searchQuery: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE cookingTime <= :maxTime ORDER BY cookingTime ASC")
    fun getRecipesByMaxTime(maxTime: Int): Flow<List<RecipeEntity>>

    @Query("SELECT COUNT(*) FROM recipes")
    fun getRecipeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
}
