package com.example.paatu

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(
    val id: Long = 0,
    val songURI: Uri? = null,
    val name: String = "",
    val author: Int = 0,
    val duration: Int = 0,
    val size: Int = 0
) : Parcelable


