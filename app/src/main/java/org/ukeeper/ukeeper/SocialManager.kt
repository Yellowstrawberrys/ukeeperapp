package org.ukeeper.ukeeper

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.ukeeper.ukeeper.db.DataManager

class SocialManager(private val activity: MainActivity, private val smsManager: SmsManager) {
    companion object {
        private lateinit var instance: SocialManager

        fun isInitialized() = ::instance.isInitialized

        fun get(): SocialManager {
            return instance;
        }
    }

    init {
        if (!isInitialized()) {
            instance = this
        }
    }

    public fun broadcastWarningMessage(db: DataManager, message: String) {
        for (a in db.getContacts()) {
            smsManager.sendTextMessage(
                a[1],
                null,
                message,
                null,
                null
            ).run {  }
        }
    }

    public fun requestPermission(context: Context) {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }
    }
}