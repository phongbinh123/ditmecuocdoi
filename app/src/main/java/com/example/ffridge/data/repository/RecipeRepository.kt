package com.example.ffridge.data.repository

import com.example.ffridge.data.local.dao.RecipeDao
import com.example.ffridge.data.mapper.toEntity
import com.example.ffridge.data.mapper.toRecipe
import com.example.ffridge.data.mapper.toRecipeList
import com.example.ffridge.data.model.Recipe
import com.example.ffridge.data.model.RecipeDifficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao
) {

    /**
     * Get all recipes
     */
    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entities ->
            entities.toRecipeList()
        }
    }

    /**
     * Get recipe by ID
     */
    suspend fun getRecipeById(id: String): Recipe? {
        return recipeDao.getRecipeById(id)?.toRecipe()
    }

    /**
     * Get favorite recipes
     */
    fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes().map { entities ->
            entities.toRecipeList()
        }
    }

    /**
     * Get recipes by difficulty
     */
    fun getRecipesByDifficulty(difficulty: RecipeDifficulty): Flow<List<Recipe>> {
        return recipeDao.getRecipesByDifficulty(difficulty.name).map { entities ->
            entities.toRecipeList()
        }
    }

    /**
     * Get recipes by max cooking time
     */
    fun getRecipesByMaxTime(maxMinutes: Int): Flow<List<Recipe>> {
        return recipeDao.getRecipesByMaxTime(maxMinutes).map { entities ->
            entities.toRecipeList()
        }
    }

    /**
     * Search recipes
     */
    fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { entities ->
            entities.toRecipeList()
        }
    }

    /**
     * Get recipe count
     */
    fun getRecipeCount(): Flow<Int> {
        return recipeDao.getRecipeCount()
    }

    /**
     * Insert recipe
     */
    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toEntity())
    }

    /**
     * Insert multiple recipes
     */
    suspend fun insertRecipes(recipes: List<Recipe>) {
        val entities = recipes.map { it.toEntity() }
        recipeDao.insertRecipes(entities)
    }

    /**
     * Update recipe
     */
    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe.toEntity())
    }

    /**
     * Delete recipe
     */
    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe.toEntity())
    }

    /**
     * Delete recipe by ID
     */
    suspend fun deleteRecipeById(id: String) {
        recipeDao.deleteRecipeById(id)
    }

    /**
     * Delete all recipes
     */
    suspend fun deleteAllRecipes() {
        recipeDao.deleteAllRecipes()
    }

    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(recipeId: String) {
        val recipe = getRecipeById(recipeId)
        recipe?.let {
            recipeDao.updateFavoriteStatus(recipeId, !it.isFavorite)
        }
    }

    /**
     * Set favorite status
     */
    suspend fun setFavorite(recipeId: String, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    /**
     * Get recipes that can be made with available ingredients
     */
    fun getRecipesWithAvailableIngredients(
        availableIngredients: List<String>
    ): Flow<List<Recipe>> {
        return getAllRecipes().map { recipes ->
            recipes.filter { recipe ->
                recipe.ingredients.any { ingredient ->
                    availableIngredients.any { available ->
                        ingredient.contains(available, ignoreCase = true)
                    }
                }
            }
        }
    }

    /**
     * Get quick recipes (cooking time <= 30 minutes)
     */
    fun getQuickRecipes(): Flow<List<Recipe>> {
        return getRecipesByMaxTime(30)
    }

    /**
     * Get easy recipes
     */
    fun getEasyRecipes(): Flow<List<Recipe>> {
        return getRecipesByDifficulty(RecipeDifficulty.EASY)
    }
}
