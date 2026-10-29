package com.jazzcash.awaz

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

// Phone voice, held in a static reference so it is never garbage-collected
// before the announcement finishes. Roman Urdu text is spoken with the
// English voice, which reads Latin script reliably.
object TtsHelper {
    private var tts: TextToSpeech? = null
    private var pendingText: String? = null
    private var pendingDone: (() -> Unit)? = null

    fun speak(context: Context, text: String, onDone: (() -> Unit)? = null) {
        val existing = tts
        if (existing != null) {
            existing.speak(text, TextToSpeech.QUEUE_FLUSH, null, "announce")
            return
        }
        pendingText = text
        pendingDone = onDone
        tts = TextToSpeech(context.applicationContext) { status ->
            val t = tts
            if (status == TextToSpeech.SUCCESS && t != null) {
                t.setLanguage(Locale.ENGLISH)
                t.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        pendingDone?.invoke()
                        pendingDone = null
                    }
                    override fun onError(utteranceId: String?) {
                        pendingDone?.invoke()
                        pendingDone = null
                    }
                })
                pendingText?.let { t.speak(it, TextToSpeech.QUEUE_FLUSH, null, "announce") }
                pendingText = null
            } else {
                pendingDone?.invoke()
                pendingDone = null
            }
        }
    }
}
