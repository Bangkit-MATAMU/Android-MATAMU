package com.akih.matarak.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Article (
    val title : String = "",
    val thumbnail : String = "",
    val content : String = "",
    val source: String = ""
) : Parcelable