package fi.ketjusovellus.ketjupilotti.application

import android.content.Context
import fi.ketjusovellus.ketjupilotti.main.HomeViewModel
import fi.ketjusovellus.ketjupilotti.onboarding.OnboardingViewModel
import fi.ketjusovellus.ketjupilotti.registerphone.ContagionViewModel
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { ContagionViewModel() }
}

val appModule = module {
    single { androidApplication().getSharedPreferences(PreferencesConstants.PREF_FILE_NAME, Context.MODE_PRIVATE) }
    single { ExposedReceiver(get()) }
}
