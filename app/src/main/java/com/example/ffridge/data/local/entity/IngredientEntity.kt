package com.example.ffridge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val quantity: String,
    val unit: String,
    val category: String,
    val expiryDate: Long?,
    val addedDate: Long,
    val notes: String?,
    val imageUrl: String?
)
