package fi.ketjusovellus.ketjupilotti.onboarding

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants

class OnboardingViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val idValid = MutableLiveData<Boolean>()

    fun setOnboardingCompleted(completed: Boolean) =
        sharedPreferences.edit().putBoolean(PreferencesConstants.PREF_ONBOARDING_KEY, completed).apply()

    fun onboardingCompleted(): Boolean =
        sharedPreferences.getBoolean(PreferencesConstants.PREF_ONBOARDING_KEY, false)

    fun saveEphIDtestPrefix(prefix: String) =
        sharedPreferences.edit().putString(PreferencesConstants.PREF_EPHID_PREFIX, prefix).apply()

    fun validateID(id: CharSequence?) {
        val isValid = (id?.length == 4)
        if (isValid) saveEphIDtestPrefix(id.toString())
        idValid.value = isValid
    }
}
