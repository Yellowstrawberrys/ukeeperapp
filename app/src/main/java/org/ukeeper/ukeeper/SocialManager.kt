package org.ukeeper.ukeeper

import android.content.Context
import android.telephony.SmsManager

class SocialManager(context:Context) {
    val smsManager:SmsManager = context.getSystemService(SmsManager::class.java) as SmsManager;

}