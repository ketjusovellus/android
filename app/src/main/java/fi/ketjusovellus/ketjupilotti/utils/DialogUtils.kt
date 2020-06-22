package fi.ketjusovellus.ketjupilotti.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class DialogUtils {

    companion object {
        fun showOkDialog(
            context: Context?,
            title: String?,
            message: String?,
            onClickListener: DialogInterface.OnClickListener?
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onClickListener)
            builder.create().show()
        }
    }
}
