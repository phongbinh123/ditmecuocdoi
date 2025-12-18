package com.example.ffridge.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Base repository interface for common CRUD operations
 */
interface BaseRepository<T> {
    fun getAll(): Flow<List<T>>
    suspend fun getById(id: String): T?
    suspend fun insert(item: T)
    suspend fun update(item: T)
    suspend fun delete(item: T)
    suspend fun deleteById(id: String)
}
