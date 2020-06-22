package fi.ketjusovellus.ketjupilotti.utils

class PreferencesConstants {
    companion object {
        const val PREF_FILE_NAME = "ketju_preferences"
        const val PREF_INFECTED_KEY = "i_am_infected"
        const val PREF_ONBOARDING_KEY = "onboarding_completed"
        const val PREF_EXPOSED_NOTIFICATION_LAST_EXPOSURES = "notification_last_exposures"

        // For debug and qa purposes
        const val PREF_EPHID_PREFIX = "ephid_prefix"
    }
}
