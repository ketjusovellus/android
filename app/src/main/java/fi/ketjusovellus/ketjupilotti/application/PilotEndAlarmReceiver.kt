package fi.ketjusovellus.ketjupilotti.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.dpppt.android.sdk.DP3T

class PilotEndAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            DP3T.stop(it)
            Toast.makeText(it, "Pilotti päättynyt", Toast.LENGTH_LONG).show()
        }
    }
}
