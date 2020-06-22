package fi.ketjusovellus.ketjupilotti.application

import android.app.Application
import fi.ketjusovellus.ketjupilotti.BuildConfig
import okhttp3.CertificatePinner
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.backend.models.ApplicationInfo
import org.dpppt.android.sdk.util.SignatureUtil
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.net.URL

class Application : Application() {

    private val receiver : ExposedReceiver by inject()

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            // Declare used Android context
            androidContext(this@Application)
            // Declare modules
            modules(listOf(
                viewModelModule,
                appModule
            ))
        }

        // Initialize DP-3T SDK
        initDP3T()
    }

    private fun initDP3T() {
        DP3T.init(
            this,
            ApplicationInfo(
                BuildConfig.APPLICATION_ID,
                BuildConfig.KETJU_BACKEND_URL,
                BuildConfig.KETJU_BACKEND_URL
            ),
            // Use public key for verifying server response signatures
            SignatureUtil.getPublicKeyFromBase64OrThrow(BuildConfig.KETJU_BACKEND_JWT_PUBLIC_KEY)
        )
        registerReceiver(receiver, DP3T.getUpdateIntentFilter())

        // Use pinned certificate for network communication
        if (BuildConfig.KETJU_BACKEND_CERTIFICATE_HASH.isNotBlank()) {
            val certificatePinner = CertificatePinner.Builder()
                .add(URL(BuildConfig.KETJU_BACKEND_URL).host, BuildConfig.KETJU_BACKEND_CERTIFICATE_HASH)
                .build()
            DP3T.setCertificatePinner(certificatePinner)
        }
    }
}
