package fi.ketjusovellus.ketjupilotti.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat


class NotificationUtils {

    companion object {
        fun areNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        fun openNotificationSettings(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            }
        }

        @RequiresApi(26)
        fun isChannelBlocked(channelId: String, context: Context): Boolean {
            val manager = NotificationManagerCompat.from(context)
            val channel = manager.getNotificationChannel(channelId)
            return channel != null && channel.importance == NotificationManagerCompat.IMPORTANCE_NONE
        }

        @RequiresApi(26)
        fun openChannelSettings(channelId: String, context: Context) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            context.startActivity(intent)
        }
    }
}