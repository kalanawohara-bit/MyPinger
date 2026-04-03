package com.example.netkepper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 1. Back බට්න් එක (පෙර තිරයට යාමට)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Settings තිරය වසා දමයි
        }

        // 2. Reset Pinger Status බට්න් එක (Sent/Drops 0 කිරීම)
        val btnResetPinger = findViewById<LinearLayout>(R.id.btnResetPinger)
        btnResetPinger.setOnClickListener {
            val sharedPrefs = getSharedPreferences("PingerStats", Context.MODE_PRIVATE)
            sharedPrefs.edit()
                .putInt("SENT_COUNT", 0)
                .putInt("DROP_COUNT", 0)
                .apply()

            Toast.makeText(this, "Pinger Status Reset Successfully!", Toast.LENGTH_SHORT).show()
        }

        // 3. Full App Reset බට්න් එක (සියලුම දත්ත මකා දැමීම)
        val btnFullReset = findViewById<LinearLayout>(R.id.btnFullReset)
        btnFullReset.setOnClickListener {
            val sharedPrefs = getSharedPreferences("PingerStats", Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()

            Toast.makeText(this, "Full App Reset Complete!", Toast.LENGTH_SHORT).show()
        }

        // 4. Email Us කොටස (ඊමේල් ඇප් එක විවෘත කිරීම)
        val btnEmailUs = findViewById<LinearLayout>(R.id.btnEmailUs)
        btnEmailUs.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                // ඔයාගේ ඊමේල් ලිපිනය මෙහි වෙනස් කර නැත
                data = Uri.parse("mailto:windywoharahw@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Regarding MY PINGER App")
            }

            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email..."))
            } catch (e: Exception) {
                Toast.makeText(this, "No Email app found!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}