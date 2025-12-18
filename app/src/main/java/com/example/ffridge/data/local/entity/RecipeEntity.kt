package com.example.ffridge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ffridge.data.local.database.Converters

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val cookingTime: Int,
    val difficulty: String,
    val imageUrl: String?,
    val createdAt: Long,
    val isFavorite: Boolean
)
