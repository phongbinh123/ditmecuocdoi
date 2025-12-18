package com.example.ffridge

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.ffridge.data.local.database.DatabaseProvider
import com.example.ffridge.data.local.database.FfridgeDatabase
import com.example.ffridge.data.repository.RepositoryProvider
import com.example.ffridge.util.ExpiryCheckWorker
import java.util.concurrent.TimeUnit

class FfridgeApplication : Application() {

    lateinit var database: FfridgeDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize database
        database = FfridgeDatabase.getDatabase(this)
        DatabaseProvider.initialize(this)

        // Initialize repositories
        RepositoryProvider.initialize(this)

        // Create notification channel
        createNotificationChannel()

        // Schedule periodic expiry check
        scheduleExpiryCheck()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Expiry Notifications"
            val descriptionText = "Notifications for expiring ingredients"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleExpiryCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val expiryCheckRequest = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ExpiryCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            expiryCheckRequest
        )
    }

    companion object {
        const val CHANNEL_ID = "ffridge_expiry_channel"
    }
}
