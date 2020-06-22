package fi.ketjusovellus.ketjupilotti.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import fi.ketjusovellus.ketjupilotti.R
import fi.ketjusovellus.ketjupilotti.utils.DialogUtils
import fi.ketjusovellus.ketjupilotti.utils.RequirementsUtils
import kotlinx.android.synthetic.main.fragment_onboarding_encounters.*

class EncountersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_onboarding_encounters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_next?.setOnClickListener { requestEnableBluetooth()}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ENABLE_BLUETOOTH -> requestDisableBatteryOptimizations()
            REQUEST_CODE_DISABLE_BATTERY_OPTIMIZATIONS -> requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateToNext()
                }
                else {
                    DialogUtils.showOkDialog(
                        activity,
                        getString(R.string.onboarding_permissions_location_title),
                        getString(R.string.onboarding_permissions_location_description),
                        DialogInterface.OnClickListener { _, _ -> navigateToNext() })
                }
            }
        }
    }

    private fun navigateToNext() {
        view?.findNavController()?.navigate(R.id.action_encountersFragment_to_infectionFragment)
    }

    private fun requestEnableBluetooth() {
        if (!RequirementsUtils.isBluetoothEnabled()) {
            // Ask user to enable Bluetooth first, and after it proceed to requesting
            // location access permission.
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH)
        }
        else {
            // If BT is already enabled, request next to disable battery optimizations
            requestDisableBatteryOptimizations()
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestDisableBatteryOptimizations() {
        if (!RequirementsUtils.isBatteryOptimizationDeactivated(requireContext())) {
            // Ask user to disable battery optimizations.
            val batteryIntent = Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivityForResult(batteryIntent, REQUEST_CODE_DISABLE_BATTERY_OPTIMIZATIONS)
        }
        else {
            // Otherwise continue querying for location capability permission.
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_PERMISSION_LOCATION
        )
    }

    companion object {
        private const val REQUEST_CODE_ENABLE_BLUETOOTH = 1233
        private const val REQUEST_CODE_PERMISSION_LOCATION = 1234
        private const val REQUEST_CODE_DISABLE_BATTERY_OPTIMIZATIONS = 1235
    }
}
