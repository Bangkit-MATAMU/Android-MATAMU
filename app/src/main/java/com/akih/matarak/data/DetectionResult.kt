package com.akih.matarak.data

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetectionResult(
        val id: Int,
        val image: Bitmap,
        val title: String,
        val confidence: Int
        ) : Parcelable
