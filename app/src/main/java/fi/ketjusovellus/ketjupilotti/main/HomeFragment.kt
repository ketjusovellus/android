package fi.ketjusovellus.ketjupilotti.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import fi.ketjusovellus.ketjupilotti.BuildConfig
import fi.ketjusovellus.ketjupilotti.R
import fi.ketjusovellus.ketjupilotti.dp3t.DebugDP3TActivity
import fi.ketjusovellus.ketjupilotti.utils.NotificationUtils
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants
import fi.ketjusovellus.ketjupilotti.utils.RequirementsUtils
import kotlinx.android.synthetic.main.fragment_home.*
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.DP3TCalibrationHelper
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_PERMISSION_LOCATION = 1234
        private const val FEATURE_FLAG_DEBUG_MENU = true
    }

    private val viewModel: HomeViewModel by viewModel()
    private val sharedPreferences : SharedPreferences by inject()

    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                viewModel.setBluetoothState(state == BluetoothAdapter.STATE_ON)
            }
        }
    }

    private val locationServiceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LocationManager.MODE_CHANGED_ACTION) {
                viewModel.setLocationServicesState(RequirementsUtils.isLocationServiceEnabled(requireContext()))
            }
        }
    }

    private val homeStateObserver = Observer<HomeState> { homeState ->
        val homeEnabled = (homeState == HomeState.ENABLED || homeState == HomeState.INFECTED)

        home_image.isVisible = homeEnabled
        home_disabled_state_group.isVisible = !homeEnabled
        home_ketju_status_indicator.setImageResource(if (homeEnabled) R.drawable.ic_check else R.drawable.ic_alert)
        
        if (homeState == HomeState.INFECTED) {
            home_image.pauseAnimation()
            home_got_sick_link.isVisible = false
        }

        home_title.setText(when(homeState) {
            HomeState.INFECTED -> R.string.contagion_contacts_shared_title
            HomeState.ERROR_BLUETOOTH_DISABLED -> R.string.home_ketju_inactive_bluetooth_title
            HomeState.ERROR_NOTIFICATIONS_DISABLED -> R.string.home_ketju_incative_notifications_title
            HomeState.ERROR_BATTERY_OPTIMIZATIONS_ENABLED -> R.string.home_ketju_inactive_battery_optimizations_title
            HomeState.ERROR_LOCATION_DISABLED -> R.string.home_ketju_inactive_location_title
            HomeState.ERROR_LOCATION_SERVICES_DISABLED -> R.string.home_ketju_inactive_location_services_title
            else -> R.string.home_ketju_active_title
        })

        home_description.setText(when(homeState) {
            HomeState.INFECTED -> R.string.contagion_contacts_shared_description
            HomeState.ERROR_BLUETOOTH_DISABLED -> R.string.home_ketju_inactive_bluetooth_description
            HomeState.ERROR_NOTIFICATIONS_DISABLED -> R.string.home_ketju_inactive_notifications_description
            HomeState.ERROR_BATTERY_OPTIMIZATIONS_ENABLED -> R.string.home_ketju_inactive_battery_optimizations_description
            HomeState.ERROR_LOCATION_DISABLED -> R.string.home_ketju_inactive_location_description
            HomeState.ERROR_LOCATION_SERVICES_DISABLED -> R.string.home_ketju_inactive_location_services_description
            else -> R.string.home_ketju_active_description
        })

        home_disabled_action_link.setText(when(homeState) {
            HomeState.ERROR_BLUETOOTH_DISABLED -> R.string.home_ketju_inactive_bluetooth_link
            HomeState.ERROR_NOTIFICATIONS_DISABLED -> R.string.home_ketju_inactive_notifications_link
            HomeState.ERROR_BATTERY_OPTIMIZATIONS_ENABLED -> R.string.home_ketju_inactive_battery_optimizations_link
            HomeState.ERROR_LOCATION_DISABLED -> R.string.home_ketju_inactive_location_link
            HomeState.ERROR_LOCATION_SERVICES_DISABLED -> R.string.home_ketju_inactive_location_services_link
            else -> R.string.home_ketju_active_description
        })

        home_disabled_action_link.setOnClickListener{
            when(homeState) {
                HomeState.ERROR_BLUETOOTH_DISABLED -> tryEnableBluetooth()
                HomeState.ERROR_NOTIFICATIONS_DISABLED -> tryEnableNotifications()
                HomeState.ERROR_BATTERY_OPTIMIZATIONS_ENABLED -> tryDisableBatteryOptimizations()
                HomeState.ERROR_LOCATION_DISABLED -> tryRequireLocationPermission()
                else -> {} // Nothing.
            }
        }
    }

    private val userExposedObserver = Observer<Boolean> {
        home_exposed_link.isVisible = it
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(FEATURE_FLAG_DEBUG_MENU) {
            debug_menu_button.isVisible = true
            debug_menu_button.setOnClickListener {
                startActivity(Intent(context, DebugDP3TActivity::class.java))
            }
        }

        home_disabled_action_link.movementMethod = LinkMovementMethod.getInstance()
        home_got_sick_link.movementMethod = LinkMovementMethod.getInstance()
        home_got_sick_link.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_registerPhoneFragment)
        }
        home_exposed_link.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_exposuresListFragment)
        }
        viewModel.getHomeState().observe(viewLifecycleOwner, homeStateObserver)
        viewModel.isUserExposed().observe(viewLifecycleOwner, userExposedObserver)

        startDP3T()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        context?.registerReceiver(locationServiceReceiver, IntentFilter(LocationManager.MODE_CHANGED_ACTION))
    }

    override fun onResume() {
        super.onResume()

        viewModel.setInfected(arguments?.getBoolean(PreferencesConstants.PREF_INFECTED_KEY, false))
        viewModel.setBluetoothState(RequirementsUtils.isBluetoothEnabled())
        viewModel.setLocationState(RequirementsUtils.isLocationPermissionGranted(requireContext()))
        viewModel.setLocationServicesState(RequirementsUtils.isLocationServiceEnabled(requireContext()))
        viewModel.setNotificationsEnabled(RequirementsUtils.areNotificationsEnabled(requireContext()))
        viewModel.setBatteryOptimizationState(!RequirementsUtils.isBatteryOptimizationDeactivated(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(bluetoothReceiver)
        context?.unregisterReceiver(locationServiceReceiver)
    }

    private fun startDP3T() {
        // If Ephemeral ID prefix has been defined, use it for identifiable ids.
        if (BuildConfig.BUILD_TYPE != "release") {
            val prefix = sharedPreferences.getString(PreferencesConstants.PREF_EPHID_PREFIX, null)
            prefix?.let {
                DP3TCalibrationHelper.setCalibrationTestDeviceName(requireContext(), it)
            }
        }

        // Start DP3T scanning!
        DP3T.start(requireContext())
    }

    private fun tryEnableBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.enable()
    }

    private fun tryEnableNotifications() {
        NotificationUtils.openNotificationSettings(requireContext())
    }

    private fun tryRequireLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSION_LOCATION)
    }

    @SuppressLint("BatteryLife")
    private fun tryDisableBatteryOptimizations() {
        startActivity(Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:${requireContext().packageName}"))
        )
    }
}
