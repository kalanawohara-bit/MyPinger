package com.example.netkepper

import com.example.netkepper.R
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.net.Inet4Address
import java.net.NetworkInterface

class infoFragment : Fragment() {

    private lateinit var tvStatus: TextView
    private lateinit var tvType: TextView
    private lateinit var tvIp: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        tvStatus = view.findViewById(R.id.tvStatus)
        tvType = view.findViewById(R.id.tvType)
        tvIp = view.findViewById(R.id.tvIp)
        val btnRefresh = view.findViewById<Button>(R.id.btnRefresh)

        // මුලින්ම පිටුවට යද්දී විස්තර ගන්නවා
        updateNetworkInfo()

        // Refresh බොත්තම එබුවාමත් අලුත් කරනවා
        btnRefresh.setOnClickListener {
            // 1. බොත්තම එබුවා කියලා හරියටම දැනගන්න මැසේජ් එකක් දානවා
            Toast.makeText(requireContext(), "Checking Network...", Toast.LENGTH_SHORT).show()

            // 2. අකුරු වල පාට සහ විස්තරය වෙනස් කරනවා (ඇසට පෙනෙන්න)
            tvStatus.text = "Checking..."
            tvStatus.setTextColor(android.graphics.Color.parseColor("#888888")) // අළු පාට
            tvType.text = "Please wait..."
            tvIp.text = "..."

            // 3. තත්පර 1කට පස්සේ (1000ms) අලුත් විස්තරේ පෙන්වනවා
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAdded) {
                    updateNetworkInfo()
                }
            }, 1000)
        }

        return view
    }

    private fun updateNetworkInfo() {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)

        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            tvStatus.text = "Online"
            tvStatus.setTextColor(android.graphics.Color.parseColor("#00FF00")) // කොළ පාට

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                tvType.text = "WiFi Connection"
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                tvType.text = "Mobile Data"
            } else {
                tvType.text = "Other Network"
            }

            tvIp.text = getLocalIpAddress()
        } else {
            tvStatus.text = "Offline"
            tvStatus.setTextColor(android.graphics.Color.parseColor("#FF0044")) // රතු පාට
            tvType.text = "Disconnected"
            tvIp.text = "0.0.0.0"
        }
    }

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "Unknown"
                    }
                }
            }
        } catch (e: Exception) {
            return "Error"
        }
        return "Not Found"
    }
}