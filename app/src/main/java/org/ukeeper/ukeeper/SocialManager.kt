package org.ukeeper.ukeeper

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.ukeeper.ukeeper.db.DataManager

class SocialManager(private val activity: MainActivity, private val context:Context) {
    private val smsManager:SmsManager = context.getSystemService(SmsManager::class.java) as SmsManager;

    public fun broadcastWarningMessage(db: DataManager, message: String) {
        for (a in db.getContacts()) {
            smsManager.sendTextMessage(
                a[1],
                null,
                message,
                null,
                null
            )
        }
    }

    public fun requestPermission() {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }
    }
}