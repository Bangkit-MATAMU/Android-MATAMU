package com.akih.matarak.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetectionResult(
        var id: String = "",
        val imageUrl: String = "",
        val title: String = "",
        val time: String = "",
        val confidence: Int = 1
        ) : Parcelable {
                @Exclude
                fun toMap(): Map<String, Any?> {
                        return mapOf(
                                "image_url" to imageUrl,
                                "title" to title,
                                "time" to time,
                                "confidence" to confidence
                        )
                }

}
