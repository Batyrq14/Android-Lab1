package com.example.lab1.service

import android.R
import android.app.*
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.lab1.MainActivity
import java.io.IOException

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val channelId = "MusicServiceChannel"

    companion object {
        const val ACTION_START = "com.example.androidlab.action.START"
        const val ACTION_PAUSE = "com.example.androidlab.action.PAUSE"
        const val ACTION_RESUME = "com.example.androidlab.action.RESUME"
        const val ACTION_STOP = "com.example.androidlab.action.STOP"

        var isPlaying = false
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
        try {
            val descriptor: AssetFileDescriptor = assets.openFd("toosieslide.mp3")
            mediaPlayer?.apply {
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                prepare()
            }
            descriptor.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Error loading music file", Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                startMusic()
            }
            ACTION_PAUSE -> {
                pauseMusic()
            }
            ACTION_RESUME -> {
                resumeMusic()
            }
            ACTION_STOP -> {
                stopMusic()
            }
        }
        return START_NOT_STICKY
    }

    private fun startMusic() {
        mediaPlayer?.start()
        isPlaying = true
        startForeground(1, createNotification("Playing Music"))
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
        startForeground(1, createNotification("Paused"))
    }

    private fun resumeMusic() {
        mediaPlayer?.start()
        isPlaying = true
        startForeground(1, createNotification("Playing Music"))
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        isPlaying = false
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(status: String): Notification {
        // Create intent for the activity
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Create media control intents
        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_RESUME
        }
        val pausePendingIntent = PendingIntent.getService(
            this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_media_pause,
                if (isPlaying) "Pause" else "Resume",
                pausePendingIntent
            )
            .addAction(
                R.drawable.ic_media_ff,
                "Stop",
                stopPendingIntent
            )
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }
}