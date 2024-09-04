package org.ukeeper.ukeeper

import android.content.Context
import android.telephony.SmsManager
import org.ukeeper.ukeeper.db.DataManager

class SocialManager(context:Context) {
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
}