package com.example.netkepper

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.netkepper.R

class DataFragment : Fragment() {

    private lateinit var tvSessionData: TextView
    private lateinit var tvRequestsSent: TextView
    private lateinit var tvPacketLoss: TextView
    private lateinit var tvAvgPing: TextView
    private lateinit var tvSuccessRate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // අලුත් Data Layout එක ලෝඩ් කිරීම
        val view = inflater.inflate(R.layout.fragment_data, container, false)

        // XML එකේ ඇති අලුත් IDs අඳුරගැනීම
        tvSessionData = view.findViewById(R.id.tvSessionData)
        tvRequestsSent = view.findViewById(R.id.tvRequestsSent)
        tvPacketLoss = view.findViewById(R.id.tvPacketLoss)
        tvAvgPing = view.findViewById(R.id.tvAvgPing)
        tvSuccessRate = view.findViewById(R.id.tvSuccessRate)

        // දත්ත යාවත්කාලීන කිරීමේ ෆන්ක්ෂන් එක කෝල් කිරීම
        updateData()

        return view
    }

    // Data තිරයට එන සෑම විටම අලුත් දත්ත පෙන්වීමට
    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun updateData() {
        // Pinger එකේ සේව් වී ඇති දත්ත (SharedPreferences) ලබා ගැනීම
        val sharedPrefs = requireActivity().getSharedPreferences("PingerStats", Context.MODE_PRIVATE)
        val sentCount = sharedPrefs.getInt("SENT_COUNT", 0)
        val dropCount = sharedPrefs.getInt("DROP_COUNT", 0)

        // Requests Sent සහ Packet Loss තිරයේ පෙන්වීම
        tvRequestsSent.text = sentCount.toString()
        tvPacketLoss.text = dropCount.toString()

        // Success Rate (සාර්ථකතා ප්‍රතිශතය) ගණනය කිරීම
        val successRate = if (sentCount > 0) {
            ((sentCount - dropCount).toDouble() / sentCount * 100).toInt()
        } else {
            100 // කිසිවක් යවා නැතිනම් 100% ලෙස පෙන්වමු
        }
        tvSuccessRate.text = "$successRate%"

        // Session Data ගණනය කිරීම (එක් Ping එකක් ආසන්නව 64 Bytes ලෙස සලකා)
        val totalBytes = sentCount * 64
        val kbUsage = totalBytes / 1024.0
        tvSessionData.text = String.format("%.1f KB", kbUsage)
        // Avg Ping සඳහා දැනට 45ms ලෙස සකසා ඇත
        tvAvgPing.text = "45ms"
    }
}