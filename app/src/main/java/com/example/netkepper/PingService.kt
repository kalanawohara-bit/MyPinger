package com.example.netkepper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class PingService : Service() {

    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SERVICE") {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            isRunning = false
            return START_NOT_STICKY
        }

        if (!isRunning) {
            isRunning = true
            startForegroundServiceNotification()
            startPinging()
        }
        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        val channelId = "PingServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pinger Background Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("My Pinger Active")
            .setContentText("Keeping connection alive...")
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .build()
        startForeground(1, notification)
    }

    private fun startPinging() {
        // SharedPreferences (පොඩි ඩේටාබේස් එක) ඕපන් කරගන්නවා
        val sharedPrefs = getSharedPreferences("PingerStats", Context.MODE_PRIVATE)

        thread {
            while (isRunning) {
                // කලින් සේව් වෙලා තියෙන ගණන් ටික ගන්නවා
                var sentCount = sharedPrefs.getInt("SENT_COUNT", 0)
                var dropCount = sharedPrefs.getInt("DROP_COUNT", 0)

                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                var pingTime: Long = 0

                try {
                    val startTime = System.currentTimeMillis()
                    val url = URL("https://www.hutch.lk")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "HEAD"
                    connection.connectTimeout = 5000

                    val responseCode = connection.responseCode
                    pingTime = System.currentTimeMillis() - startTime

                    if (responseCode == 200) {
                        sentCount++ // සාර්ථකයි නම් Sent වලට 1ක් එකතු කරනවා
                        sendLogToUI("[$time] Ping: ${pingTime}ms", sentCount, dropCount)
                    } else {
                        dropCount++ // අසාර්ථකයි නම් Drops වලට 1ක් එකතු කරනවා
                        sendLogToUI("[$time] Failed (Code: $responseCode)", sentCount, dropCount)
                    }
                } catch (e: Exception) {
                    dropCount++ // Error එකක් ආවත් ඒක Drop එකක් විදිහට සලකනවා
                    sendLogToUI("[$time] Error: Timeout/Loss", sentCount, dropCount)
                }

                // අලුත් ගණන් ටික ආයෙත් ෆෝන් එකේ සේව් කරනවා (ඇප් එක close කරත් තියෙන්න)
                sharedPrefs.edit()
                    .putInt("SENT_COUNT", sentCount)
                    .putInt("DROP_COUNT", dropCount)
                    .apply()

                val sleepTime = 5000 - pingTime
                if (sleepTime > 0 && isRunning) {
                    Thread.sleep(sleepTime)
                }
            }
        }
    }

    // දැන් පණිවිඩය යවද්දී ගණන් කරපු Sent සහ Drops අගයනුත් තිරයට යවනවා
    private fun sendLogToUI(message: String, sent: Int, drops: Int) {
        val intent = Intent("PingUpdate")
        intent.setPackage(packageName)
        intent.putExtra("logMessage", message)
        intent.putExtra("sentCount", sent)
        intent.putExtra("dropCount", drops)
        sendBroadcast(intent)
    }
}