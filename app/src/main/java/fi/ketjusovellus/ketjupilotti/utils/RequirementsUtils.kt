package fi.ketjusovellus.ketjupilotti.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class RequirementsUtils {

    companion object {
        fun isLocationPermissionGranted(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        fun isLocationServiceEnabled(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lm = context.getSystemService(Context.LOCATION_SERVICE)
                lm != null && (lm as LocationManager).isLocationEnabled
            } else {
                val mode = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            }
        }

        fun isBluetoothEnabled(): Boolean {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return (bluetoothAdapter != null && bluetoothAdapter.isEnabled)
        }

        fun areNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun isBatteryOptimizationDeactivated(context: Context): Boolean {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
    }
}
