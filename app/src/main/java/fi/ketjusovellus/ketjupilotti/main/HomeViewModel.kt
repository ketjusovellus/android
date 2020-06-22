package fi.ketjusovellus.ketjupilotti.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants

enum class HomeState {
    ENABLED,
    ERROR_BLUETOOTH_DISABLED,
    ERROR_LOCATION_DISABLED,
    ERROR_LOCATION_SERVICES_DISABLED,
    ERROR_BATTERY_OPTIMIZATIONS_ENABLED,
    ERROR_NOTIFICATIONS_DISABLED,
    INFECTED
}

class HomeViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val currentState = MutableLiveData(HomeState.ENABLED)

    private var infected = sharedPreferences.getBoolean(PreferencesConstants.PREF_INFECTED_KEY, false)
    private var bluetoothEnabled = true
    private var locationAllowed = true
    private var locationServicesEnabled = true
    private var notificationsAllowed = true
    private var batteryOptimizationsEnabled = false

    fun getHomeState(): LiveData<HomeState> {
        return currentState
    }

    fun isUserExposed(): LiveData<Boolean> = liveData {
        emit(
            sharedPreferences.getInt(PreferencesConstants.PREF_EXPOSED_NOTIFICATION_LAST_EXPOSURES,0) > 0
        )
    }

    private fun checkCurrentState() {
        val state = determineCurrentState()

        if (currentState.value != state) {
            currentState.value = state
        }
    }

    private fun determineCurrentState(): HomeState {
        if (infected) return HomeState.INFECTED
        if (!bluetoothEnabled) return HomeState.ERROR_BLUETOOTH_DISABLED
        if (!locationAllowed) return HomeState.ERROR_LOCATION_DISABLED
        if (!locationServicesEnabled) return HomeState.ERROR_LOCATION_SERVICES_DISABLED
        if (!notificationsAllowed) return HomeState.ERROR_NOTIFICATIONS_DISABLED
        if (batteryOptimizationsEnabled) return HomeState.ERROR_BATTERY_OPTIMIZATIONS_ENABLED

        return HomeState.ENABLED
    }

    fun setBluetoothState(enabled: Boolean) {
        bluetoothEnabled = enabled
        checkCurrentState()
    }

    fun setLocationState(allowed: Boolean) {
        locationAllowed = allowed
        checkCurrentState()
    }

    fun setLocationServicesState(enabled: Boolean) {
        locationServicesEnabled = enabled
        checkCurrentState()
    }

    fun setBatteryOptimizationState(enabled: Boolean) {
        batteryOptimizationsEnabled = enabled
        checkCurrentState()
    }

    fun setNotificationsEnabled(allowed: Boolean) {
        notificationsAllowed = allowed
        checkCurrentState()
    }

    fun setInfected(completed: Boolean?) {
        completed?.let {
            sharedPreferences.edit().putBoolean(PreferencesConstants.PREF_INFECTED_KEY, it).apply()
            infected = it
            checkCurrentState()
        }
    }
}
