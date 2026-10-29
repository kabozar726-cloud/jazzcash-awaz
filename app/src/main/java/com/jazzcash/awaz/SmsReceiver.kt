package com.jazzcash.awaz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

// Runs on every incoming SMS, even when the app is closed.
// JazzCash payment alerts are spoken aloud; everything else is ignored.
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        if (!LogHelper.isEnabled(context)) return
        val pending = goAsync()
        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isEmpty()) {
                pending.finish()
                return
            }
            // Multi-part SMS arrives as parts: join them into one message.
            val sender = messages[0].displayOriginatingAddress
            val body = messages.joinToString("") { it.messageBody ?: "" }
            if (!SmsParser.isJazzCash(sender, body)) {
                pending.finish()
                return
            }
            val announcement = SmsParser.buildAnnouncement(body)
            if (announcement == null) {
                pending.finish()
                return
            }
            LogHelper.add(context, announcement)
            // Hold the receiver alive until the voice finishes.
            TtsHelper.speak(context, announcement) { pending.finish() }
        } catch (e: Exception) {
            // Never crash on an incoming SMS.
            try {
                pending.finish()
            } catch (ignored: Exception) {
            }
        }
    }
}
