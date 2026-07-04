package com.example.mcocarenabot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat

/**
 * Serviço em primeiro plano: mantém uma notificação persistente (transparência
 * necessária pra automação rodando em segundo plano) e um wake lock parcial
 * pra manter a CPU ativa. A TELA precisa continuar ligada pro jogo renderizar —
 * configure o tempo limite da tela pro maior valor disponível nos ajustes do Android.
 */
class BotForegroundService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MCOC Arena Bot ativo")
            .setContentText("Automação de farm rodando em segundo plano")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MCOCArenaBot::WakeLock"
        ).apply { acquire(4 * 60 * 60 * 1000L) } // limite de segurança: 4h
    }

    override fun onDestroy() {
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Bot de Arena", NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "mcoc_bot_channel"
        const val NOTIFICATION_ID = 1001
    }
}
