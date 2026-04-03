package com.example.netkepper

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Notification අවසරය ඉල්ලීම
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // මුලින්ම ඇප් එක ඕපන් වෙද්දී PingerFragment එක පෙන්වන්න
        if (savedInstanceState == null) {
            loadFragment(PingerFragment())
        }

        // යට තියෙන මෙනු එකේ බොත්තම් ඔබද්දී පිටු මාරු කිරීම
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pinger -> {
                    loadFragment(PingerFragment())
                    true
                }
                R.id.nav_info -> {
                    loadFragment(infoFragment()) // තවම මේ පිටුව හිස්
                    true
                }
                R.id.nav_data -> {
                    loadFragment(DataFragment()) // තවම මේ පිටුව හිස්
                    true
                }
                else -> false
            }
        }
    }

    // පිටුව රාමුව ඇතුළට දාන පොදු Function එක
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}