package com.example.ffridge.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val cookingTime: Int, // minutes
    val difficulty: RecipeDifficulty,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) : Parcelable

enum class RecipeDifficulty {
    EASY, MEDIUM, HARD
}
