package com.example.paatu

import android.Manifest

object Constants {

    const val REQ_MULTIPLE_PERMISSIONS_CODE = 100

    val PERMISSIONS_LIST = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "CHANNEL_ID_NAME"
    const val NOTIFICATION_ID = 199

    const val PLAY_MUSIC = "PLAY_MUSIC"
    const val PLAY_ALL_MUSIC = "PLAY_ALL_MUSIC"
    const val PLAY_PAUSE_MUSIC = "PAUSE_MUSIC"
    const val SKIP_PREVIOUS = "SKIP_PREVIOUS"
    const val SKIP_NEXT = "SKIP_NEXT"




}