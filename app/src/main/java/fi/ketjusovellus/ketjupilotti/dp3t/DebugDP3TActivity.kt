package fi.ketjusovellus.ketjupilotti.dp3t

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import fi.ketjusovellus.ketjupilotti.BuildConfig
import fi.ketjusovellus.ketjupilotti.R
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants
import kotlinx.android.synthetic.main.activity_debug_dp3t.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.internal.database.Database
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DebugDP3TActivity : AppCompatActivity() {

    private lateinit var database: Database
    private val sharedPreferences : SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_dp3t)
        database = Database(this@DebugDP3TActivity)
        val status = DP3T.getStatus(this)
        fetchHandshakes()
        version_text.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        identifier_text.text = sharedPreferences.getString(PreferencesConstants.PREF_EPHID_PREFIX, "")
        contacts_text.text = status.numberOfContacts.toString()
        receiving_text.text = status.isReceiving.toString()
        advertising_text.text = status.isAdvertising.toString()
        val lastSyncDate = Date(status.lastSyncDate)
        val format = SimpleDateFormat("MM-dd HH:mm")
        last_sync_text.text = format.format(lastSyncDate)
        infection_status_text.text = status.infectionStatus.name
        export_button.setOnClickListener {
            exportData()
        }
    }

    private fun exportData() {
        CoroutineScope(Dispatchers.IO).launch {

            val context = this@DebugDP3TActivity
            val path = File(context.filesDir, "dump")
            path.mkdirs()
            val prefix = sharedPreferences.getString(PreferencesConstants.PREF_EPHID_PREFIX, "")
            val timeStamp = SimpleDateFormat("dd.MM.yyyy_HH.mm").format(Date(System.currentTimeMillis()))
            val newFile = File(path, "Ketju_db_${prefix}_${timeStamp}.sqlite")
            val contentUri = getUriForFile(context, "fi.ketjusovellus.ketjupilotti.fileprovider", newFile)
            if (newFile.exists()) newFile.delete()
            newFile.createNewFile()
            database.exportTo(this@DebugDP3TActivity, newFile.outputStream()) {}

            withContext(Dispatchers.Main) {
                // share file via intent
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("ketjudev@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Ketju_db_${prefix}_${timeStamp}")
                }
                startActivity(Intent.createChooser(shareIntent, "Database dump"))
            }
        }
    }

    private fun fetchHandshakes() {
        CoroutineScope(Dispatchers.IO).launch {
            val handshakes = database.handshakes
            withContext(Dispatchers.Main) {
                handshakes_text.text = handshakes.size.toString()
            }
        }
    }

}
