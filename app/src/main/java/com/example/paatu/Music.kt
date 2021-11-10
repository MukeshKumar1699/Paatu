package com.example.paatu

import android.net.Uri

data class Music(
    val id: Long,
    val songURI: Uri,
    val name: String,
    val author: Int,
    val duration: Int,
    val size: Int
)


