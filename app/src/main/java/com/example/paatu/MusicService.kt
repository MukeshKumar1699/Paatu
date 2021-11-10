package com.example.paatu

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
    }

    private fun initMusic(songUri: Uri) {

        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, songUri)
            mediaPlayer.start()
            broadcastIntent()
        } else {
            mediaPlayer = MediaPlayer.create(this, songUri)
            mediaPlayer.start()
        }

    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val song = intent?.getStringExtra("SongUri")
        val songName = intent?.getStringExtra("SongName")

        song?.let {
            val songUri: Uri = Uri.parse(song)
            generateForegroundNotification(songName)

            if (intent.action.equals("PLAY")) {
                initMusic(songUri)
            } else if (intent.action.equals("PAUSE")) {
                pauseMusic()
            }
        }

        return START_STICKY
    }


    private fun generateForegroundNotification(songName: String?) {

        val astronautImage = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.astronaut
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intentMainLanding, 0)
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

            if (mNotificationManager == null) {
                mNotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("chats_group", "Chats")
                )
                val notificationChannel =
                    NotificationChannel(
                        "service_channel", "Service Notifications",
                        NotificationManager.IMPORTANCE_MIN
                    )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }

            val builder = NotificationCompat.Builder(this, "service_channel")

            //Prev intent
            val prevIntent = Intent()
            prevIntent.action = "Prev"
            val pendingIntentPrev: PendingIntent = PendingIntent.getBroadcast(
                this,
                12345,
                prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            //Play intent
            val playIntent = Intent()
            playIntent.action = "Prev"
            val pendingIntentPlay: PendingIntent = PendingIntent.getBroadcast(
                this,
                12345,
                prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            //Next intent
            val nextIntent = Intent()
            nextIntent.action = "Prev"
            val pendingIntentNext: PendingIntent = PendingIntent.getBroadcast(
                this,
                12345,
                prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder
                .setContentTitle(songName)
                .setTicker(songName)
                .setContentText("Touch to open")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(astronautImage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "PREVIOUS", pendingIntentPrev)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "PLAY", pendingIntentPlay)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "NEXT", pendingIntentNext)


            if (iconNotification != null) {
                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
            }
            builder.color = resources.getColor(R.color.purple_200)
            notification = builder.build()
            startForeground(mNotificationId, notification)
        }
    }


    fun broadcastIntent() {
        val intent = Intent()
        intent.action = "com.tutorialspoint.CUSTOM_INTENT"
        intent.putExtra("MediaPlayerProgress", mediaPlayer.currentPosition.toString())
        sendBroadcast(intent)
    }

    fun pauseMusic() {
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }

    companion object {

        //Notification for ON-going
        private var iconNotification: Bitmap? = null
        private var notification: Notification? = null
        var mNotificationManager: NotificationManager? = null
        private val mNotificationId = 123
    }


}

