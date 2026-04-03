package com.example.netkepper

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.netkepper.R

class PingerFragment : Fragment() {

    private var isRunning = false
    private lateinit var tvLogConsole: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var tvStatus: TextView
    private lateinit var glowRing: CardView
    private lateinit var powerIcon: ImageView
    private lateinit var btnPowerContainer: FrameLayout

    private val pingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("logMessage")
            if (message != null) {
                appendLog(tvLogConsole, scrollView, message)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pinger, container, false)

        tvLogConsole = view.findViewById(R.id.tvLogConsole)
        scrollView = view.findViewById(R.id.scrollView)
        tvStatus = view.findViewById(R.id.tvStatus)
        glowRing = view.findViewById(R.id.glowRing)
        powerIcon = view.findViewById(R.id.powerIcon)
        btnPowerContainer = view.findViewById(R.id.btnPowerContainer)

        btnPowerContainer.setOnClickListener {
            if (!isRunning) {
                startPinger()
            } else {
                stopPinger()
            }
        }

        val settingsIcon = view.findViewById<ImageView>(R.id.btnOpenSettings)
        settingsIcon?.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    // UI එක කොළ පාට (Pinging) කරන ෆන්ක්ෂන් එක
    private fun startPinger() {
        isRunning = true
        tvStatus.text = "STATUS: PINGING"
        tvStatus.setTextColor(Color.parseColor("#00E5FF"))
        glowRing.setCardBackgroundColor(Color.parseColor("#00E5FF"))
        powerIcon.setColorFilter(Color.parseColor("#00E5FF"))

        appendLog(tvLogConsole, scrollView, "--- Service Started ---")

        val serviceIntent = Intent(requireContext(), PingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    // UI එක රතු පාට (Disconnected) කරන ෆන්ක්ෂන් එක
    private fun stopPinger() {
        isRunning = false
        tvStatus.text = "STATUS: DISCONNECTED"
        tvStatus.setTextColor(Color.parseColor("#FF4444"))
        glowRing.setCardBackgroundColor(Color.parseColor("#FF4444"))
        powerIcon.setColorFilter(Color.parseColor("#FF4444"))

        appendLog(tvLogConsole, scrollView, "--- Service Stopped ---\n")

        val serviceIntent = Intent(requireContext(), PingService::class.java)
        serviceIntent.action = "STOP_SERVICE"
        requireContext().startService(serviceIntent)
    }

    // Service එක දැනටමත් දුවනවද කියලා බලන ෆන්ක්ෂන් එක
    @Suppress("DEPRECATION")
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // ඇප් එක ආයෙත් ඕපන් කරන හැම වෙලාවකම වැඩ කරන කොටස
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("PingUpdate")
        ContextCompat.registerReceiver(requireContext(), pingReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        // UI එක Update කිරීම
        if (isServiceRunning(PingService::class.java)) {
            // සර්විස් එක වැඩ නම් කොළ පාට කරන්න
            isRunning = true
            tvStatus.text = "STATUS: PINGING"
            tvStatus.setTextColor(Color.parseColor("#00E5FF"))
            glowRing.setCardBackgroundColor(Color.parseColor("#00E5FF"))
            powerIcon.setColorFilter(Color.parseColor("#00E5FF"))
        } else {
            // සර්විස් එක නැවැත්තුවා නම් රතු පාට කරන්න
            isRunning = false
            tvStatus.text = "STATUS: DISCONNECTED"
            tvStatus.setTextColor(Color.parseColor("#FF4444"))
            glowRing.setCardBackgroundColor(Color.parseColor("#FF4444"))
            powerIcon.setColorFilter(Color.parseColor("#FF4444"))
        }
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(pingReceiver)
    }

    private fun appendLog(textView: TextView, scrollView: ScrollView, message: String) {
        textView.append("$message\n")
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}