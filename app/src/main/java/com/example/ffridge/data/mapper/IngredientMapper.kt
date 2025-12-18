package com.example.ffridge.data.mapper

import com.example.ffridge.data.local.entity.IngredientEntity
import com.example.ffridge.data.model.Ingredient

fun IngredientEntity.toIngredient(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        category = this.category,
        expiryDate = this.expiryDate,
        addedDate = this.addedDate,
        notes = this.notes,
        imageUrl = this.imageUrl
    )
}

fun Ingredient.toEntity(): IngredientEntity {
    return IngredientEntity(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        category = this.category,
        expiryDate = this.expiryDate,
        addedDate = this.addedDate,
        notes = this.notes,
        imageUrl = this.imageUrl
    )
}

fun List<IngredientEntity>.toIngredientList(): List<Ingredient> {
    return this.map { it.toIngredient() }
}

fun List<Ingredient>.toEntityList(): List<IngredientEntity> {
    return this.map { it.toEntity() }
}
