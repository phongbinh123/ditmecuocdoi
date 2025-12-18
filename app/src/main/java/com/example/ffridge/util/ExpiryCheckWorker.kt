package com.example.ffridge.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ffridge.data.repository.IngredientRepository
import com.example.ffridge.data.repository.RepositoryProvider
import com.example.ffridge.data.repository.UserRepository
import kotlinx.coroutines.flow.first

class ExpiryCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val ingredientRepository: IngredientRepository by lazy {
        RepositoryProvider.getIngredientRepository()
    }

    private val userRepository: UserRepository by lazy {
        RepositoryProvider.getUserRepository()
    }

    override suspend fun doWork(): Result {
        return try {
            // Check if notifications are enabled
            val settings = userRepository.getSettings().first()
            if (!settings.expiryNotifications) {
                return Result.success()
            }

            // Get expiring ingredients
            val expiringIngredients = ingredientRepository
                .getExpiringIngredients(3)
                .first()

            // Show notification if there are expiring ingredients
            if (expiringIngredients.isNotEmpty()) {
                NotificationHelper.showExpiryNotification(
                    applicationContext,
                    expiringIngredients
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
