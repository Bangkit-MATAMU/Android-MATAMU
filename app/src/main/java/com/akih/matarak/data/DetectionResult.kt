package com.akih.matarak.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetectionResult(
        var id: String = "",
        val imageUrl: String = "",
        val title: String = "",
        val time: String = "",
        val confidence: Int = 1
        ) : Parcelable
