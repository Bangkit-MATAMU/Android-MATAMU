package com.akih.maps.model

import com.google.gson.annotations.SerializedName

class NearbyPlaceResponse{
    @SerializedName("status")
    lateinit var status: String

    @SerializedName("results")
    var results: List<PlaceResponse> = listOf()

    inner class PlaceResponse {
        @SerializedName("business_status")
        lateinit var businessStatus: String

        @SerializedName("geometry")
        var geometry: GeometryResponse? = null

        @SerializedName("icon")
        lateinit var iconUrl : String

        @SerializedName("name")
        lateinit var name: String

        @SerializedName("place_id")
        lateinit var id : String

        @SerializedName("vicinity")
        lateinit var vicinity: String

    }

    inner class GeometryResponse {
        @SerializedName("location")
        var location: LocationResponse? = null
    }

    inner class LocationResponse {
        @SerializedName("lat")
        var latitude: Double = 0.0

        @SerializedName("lng")
        var longitude: Double = 0.0
    }
}