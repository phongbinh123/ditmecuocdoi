package com.example.ffridge.data.mapper

import com.example.ffridge.data.local.entity.RecipeEntity
import com.example.ffridge.data.model.Recipe
import com.example.ffridge.data.model.RecipeDifficulty

fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        description = this.description,
        ingredients = this.ingredients,
        instructions = this.instructions,
        cookingTime = this.cookingTime,
        difficulty = RecipeDifficulty.valueOf(this.difficulty),
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        isFavorite = this.isFavorite
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        ingredients = this.ingredients,
        instructions = this.instructions,
        cookingTime = this.cookingTime,
        difficulty = this.difficulty.name,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        isFavorite = this.isFavorite
    )
}

fun List<RecipeEntity>.toRecipeList(): List<Recipe> {
    return this.map { it.toRecipe() }
}

fun List<Recipe>.toRecipeEntityList(): List<RecipeEntity> {
    return this.map { it.toEntity() }
}
