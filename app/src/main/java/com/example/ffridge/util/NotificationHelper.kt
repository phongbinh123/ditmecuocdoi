package com.example.ffridge.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ffridge.FfridgeApplication
import com.example.ffridge.MainActivity
import com.example.ffridge.R
import com.example.ffridge.data.model.Ingredient

object NotificationHelper {

    private const val NOTIFICATION_ID = 1001

    fun showExpiryNotification(
        context: Context,
        expiringIngredients: List<Ingredient>
    ) {
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (expiringIngredients.size == 1) {
            "1 ingredient expiring soon"
        } else {
            "${expiringIngredients.size} ingredients expiring soon"
        }

        val message = if (expiringIngredients.size <= 3) {
            expiringIngredients.joinToString(", ") { it.name }
        } else {
            "${expiringIngredients.take(3).joinToString(", ") { it.name }} and more"
        }

        val notification = NotificationCompat.Builder(context, FfridgeApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
