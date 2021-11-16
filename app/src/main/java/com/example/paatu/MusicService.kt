package com.example.paatu

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData

class MusicService : Service() {

    private var musicList: ArrayList<Music> = ArrayList()

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
    }

     fun initMusic(item: Int) {

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, musicList[item].songURI)
            mediaPlayer.start()

        } else {
            mediaPlayer = MediaPlayer.create(this, musicList[item].songURI)
            mediaPlayer.start()
        }

        updateToUI()

        mediaPlayer.setOnCompletionListener {

            if (currentItemPlaying + 1 < musicList.size) {
                currentItemPlaying++
                initMusic(currentItemPlaying)
            } else {
                mNotificationManager?.cancel(Constants.NOTIFICATION_ID)
                stopForeground(true)
                stopSelf()
            }
        }

    }

    private fun updateToUI() {

        val mSeekbarUpdateHandler = Handler()
        val mUpdateSeekbar: Runnable = object : Runnable {
            override fun run() {
                musicPositionLiveData.postValue(mediaPlayer.currentPosition)
                mSeekbarUpdateHandler.postDelayed(this, 50)
            }
        }
        musicLiveData.postValue(musicList[currentItemPlaying])
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

        generateForegroundNotification(musicList[currentItemPlaying].name)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        musicList = intent?.getParcelableArrayListExtra<Music>("SongList") as ArrayList<Music>

        when (intent.action) {

            Constants.PLAY_MUSIC -> {

                currentItemPlaying = intent.getIntExtra("ItemPosition", 0)
                initMusic(currentItemPlaying)
            }

            Constants.PLAY_ALL_MUSIC -> {
                initMusic(currentItemPlaying)
            }

            Constants.SKIP_PREVIOUS -> {
                if (currentItemPlaying > 0) {
                    currentItemPlaying--
                    initMusic(currentItemPlaying)
                }
            }

            Constants.SKIP_NEXT -> {
                currentItemPlaying++
                initMusic(currentItemPlaying)
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
            val intentMainLanding = Intent(this, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    intentMainLanding,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
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
                        Constants.NOTIFICATION_CHANNEL_ID,
                        Constants.NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_MIN
                    )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }

            val builder = NotificationCompat.Builder(this, "service_channel")

            //Prev intent
            intentMainLanding.action = Constants.SKIP_PREVIOUS
            val pendingIntentPrev: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                intentMainLanding,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            //Play intent
            intentMainLanding.action = Constants.PLAY_MUSIC
            val pendingIntentPlay: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                intentMainLanding,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            //Next intent
            intentMainLanding.action = Constants.SKIP_NEXT
            val pendingIntentNext: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                intentMainLanding,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder
                .setContentTitle(songName)
                .setTicker(songName)
                .setContentText("Touch to open")
                .setColor(resources.getColor(R.color.purple_200))
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
            notification = builder.build()
            startForeground(Constants.NOTIFICATION_ID, notification)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }

    companion object {

        //Notification for ON-going
        private var iconNotification: Bitmap? = null
        private var notification: Notification? = null
        private var mNotificationManager: NotificationManager? = null

        var mediaPlayer = MediaPlayer()
        val musicLiveData = MutableLiveData<Music>()
        val musicPositionLiveData = MutableLiveData<Int>()

        var currentItemPlaying: Int = 0
    }

}

