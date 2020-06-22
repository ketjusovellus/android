package fi.ketjusovellus.ketjupilotti

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import fi.ketjusovellus.ketjupilotti.main.MainActivity
import fi.ketjusovellus.ketjupilotti.onboarding.OnboardingActivity
import fi.ketjusovellus.ketjupilotti.onboarding.OnboardingViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val onboardingViewModel : OnboardingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (onboardingViewModel.onboardingCompleted()) {
            startIntent(Intent(this, MainActivity::class.java), 800L)
        }
        else {
            startIntent(Intent(this, OnboardingActivity::class.java), 800L)
        }
    }

    private fun startIntent(intent: Intent, delay: Long) {
        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, delay)
    }

}
