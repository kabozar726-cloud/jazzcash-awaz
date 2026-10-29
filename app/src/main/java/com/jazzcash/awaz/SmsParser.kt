package com.jazzcash.awaz

// Parses JazzCash payment SMS alerts into a spoken announcement.
// JazzCash sends an SMS like: "You have received Rs. 5,000 from Ahmed Khan..."
object SmsParser {

    fun isJazzCash(sender: String?, body: String): Boolean {
        val s = sender ?: ""
        return s.contains("JAZZ", ignoreCase = true) || s == "3737" ||
            body.contains("jazzcash", ignoreCase = true)
    }

    // Only incoming money is announced — never sent money or debits.
    fun isIncomingMoney(body: String): Boolean {
        return Regex("(?i)\\b(received|credited|credit)\\b").containsMatchIn(body)
    }

    fun parseAmount(body: String): String? {
        val m = Regex("(?i)Rs\\.?\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)").find(body)
            ?: return null
        val cleaned = m.groupValues[1].replace(",", "")
        return if (cleaned.endsWith(".00")) cleaned.dropLast(3) else cleaned
    }

    fun parseSender(body: String): String? {
        val m = Regex("(?i)\\bfrom\\s+([^,.\\n()]+)").find(body) ?: return null
        val raw = m.groupValues[1].trim().trimEnd('.', ' ')
        if (raw.isEmpty() || raw.equals("JazzCash", ignoreCase = true)) return null
        return raw
    }

    // Roman Urdu announcement, spoken by the phone's voice.
    // Returns null when the SMS is not an incoming-money alert.
    fun buildAnnouncement(body: String): String? {
        if (!isIncomingMoney(body)) return null
        val amount = parseAmount(body)
        if (amount == null) return body // fallback: speak the message itself
        val sender = parseSender(body)
        return if (sender != null) {
            "Aap ko $amount rupay mile hain, $sender se."
        } else {
            "Aap ko $amount rupay mile hain."
        }
    }
}
