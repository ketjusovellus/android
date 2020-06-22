package fi.ketjusovellus.ketjupilotti

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fi.ketjusovellus.ketjupilotti.application.PilotEndAlarmReceiver
import org.dpppt.android.sdk.DP3T
import java.text.SimpleDateFormat

abstract class BaseActivity : AppCompatActivity() {

    private val expirationDate = SimpleDateFormat("dd-MM-yyyy-hh:mm").parse("01-07-2020-00:00").time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (System.currentTimeMillis() > expirationDate) {
            DP3T.stop(this)
            Toast.makeText(this, "Pilotti päättynyt", Toast.LENGTH_LONG).show()
            finishAndRemoveTask()
        }

        val intent = Intent(this, PilotEndAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            800,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, expirationDate, pendingIntent)

    }
}
