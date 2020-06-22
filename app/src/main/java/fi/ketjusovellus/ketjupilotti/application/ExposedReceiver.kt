package fi.ketjusovellus.ketjupilotti.application

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import fi.ketjusovellus.ketjupilotti.R
import fi.ketjusovellus.ketjupilotti.main.MainActivity
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.InfectionStatus
import org.dpppt.android.sdk.internal.database.Database

class ExposedReceiver(
    private val sharedPreferences : SharedPreferences
) : BroadcastReceiver() {

    private fun showExposedNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.home_nav_graph)
            .setDestination(R.id.exposuresListFragment)
            .createPendingIntent()
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle(context.getString(R.string.notification_exposed_title))
            .setContentText(context.getString(R.string.notification_exposed_text))
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                context.getString(R.string.notification_exposed_text)
            ))
            .setSmallIcon(R.mipmap.ketju_notification)
            .setContentIntent(pendingIntent)
            .build()
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(900, notification)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val database = Database(context)
            val exposureDays = database.exposureDays
            val lastNumberOfExposures = sharedPreferences.getInt(PreferencesConstants.PREF_EXPOSED_NOTIFICATION_LAST_EXPOSURES, 0)
            sharedPreferences.edit().putInt(PreferencesConstants.PREF_EXPOSED_NOTIFICATION_LAST_EXPOSURES, exposureDays.size).apply()
            if(exposureDays.size> lastNumberOfExposures) {
                val status = DP3T.getStatus(context)
                when (status.infectionStatus) {
                    InfectionStatus.EXPOSED -> {
                        showExposedNotification(context)
                    }
                    else -> {}
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        val channelName: String = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager?.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL = "ketju-channel"
    }

}