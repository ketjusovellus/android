package fi.ketjusovellus.ketjupilotti.registerphone

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ContagionViewModel : ViewModel() {

    // State variables for RegisterPhoneFragment
    val phoneNumberValid = MutableLiveData<Boolean>()
    private var phoneNumber = ""

    // State variables for PinVerificationFragment
    val otpValid = MutableLiveData<Boolean>()
    private var oneTimePassword = ""

    fun validatePhoneNumber(number: CharSequence?) {
        phoneNumberValid.value = Patterns.PHONE.matcher(number ?: "").matches()
    }

    fun setPhoneNumber(phone: CharSequence?) {
        phoneNumber = phone?.toString() ?: ""
    }

    fun validateOTP(otp: CharSequence?) {
        if (otp != null && otp.length == 6) {
            val dayNbr = otp.substring(otp.length - 2).toInt()
            if (dayNbr in 1..31) {
                otpValid.value = true
                oneTimePassword = otp.toString()
            }
            else {
                otpValid.value = false
            }
        }
        else {
            otpValid.value = false
        }
    }

    // OTP's two final numbers indicate the day of month when the user has become contagious.
    fun getContagionDate(): Date {
        val dayStr = oneTimePassword.substring(oneTimePassword.length - 2)
        val dayNbr = dayStr.toInt()

        val calendar: Calendar = GregorianCalendar()
        if (dayNbr > calendar.get(Calendar.DAY_OF_MONTH)) {
            // Must be from last month, subtract one from month and set the infected date.
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DAY_OF_MONTH, dayNbr)
        }
        else {
            // Is from this month, just set the infected date.
            calendar.set(Calendar.DAY_OF_MONTH, dayNbr)
        }

        return Date(calendar.timeInMillis)
    }

    fun getExposeeAuthCode(): String {
        return "$phoneNumber//$oneTimePassword"
    }
}
