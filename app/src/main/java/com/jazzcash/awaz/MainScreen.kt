package com.jazzcash.awaz

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(LogHelper.isEnabled(context)) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var log by remember { mutableStateOf(LogHelper.read(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("JazzCash Awaz", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Jab JazzCash mein paisay aayein ge, yeh app khud bol kar bataye gi ke kitne paisay aur kis se aaye hain.")
        Spacer(Modifier.height(16.dp))
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (enabled) "Announcement ON hai" else "Announcement OFF hai",
                    Modifier.weight(1f)
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                        LogHelper.setEnabled(context, it)
                    }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        if (!hasPermission) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("SMS permission zaroori hai, warna app JazzCash ka SMS nahi parh sakegi.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.RECEIVE_SMS) }) {
                        Text("SMS Permission dein")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        } else {
            Text("SMS permission mil gayi hai.")
            Spacer(Modifier.height(12.dp))
        }
        Button(
            onClick = {
                val sample = "Aap ko 5000 rupay mile hain, Ahmed Khan se."
                TtsHelper.speak(context, sample)
                LogHelper.add(context, "$sample (Test)")
                log = LogHelper.read(context)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Test Announcement suno") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { log = LogHelper.read(context) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("List refresh karein") }
        Spacer(Modifier.height(16.dp))
        Text("Aakhri announcements", style = MaterialTheme.typography.titleMedium)
        if (log.isEmpty()) Text("Abhi koi announcement nahi hui.")
        log.forEach { line ->
            Text(line, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Zaroori: JazzCash ka SMS alert ON rakhein. Agar SMS nahi aayega to announcement nahi hogi.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
