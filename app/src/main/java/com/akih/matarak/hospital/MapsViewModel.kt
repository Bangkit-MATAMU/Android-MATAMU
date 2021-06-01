package com.akih.matarak.hospital

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akih.matarak.data.NearbyPlaceResponse
import com.akih.matarak.webservice.NetworkModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {
    private val places = MutableLiveData<List<NearbyPlaceResponse.PlaceResponse>>()
    private val state = MutableLiveData<MapsFragmentState>()
    private val api = NetworkModule.api()

    private fun setLoading(b: Boolean){
        state.value = MapsFragmentState.IsLoading(b)
    }

    private fun showToast(msg: String){
        state.value = MapsFragmentState.ShowToast(msg)
    }

    fun nearby(lat: String, lng : String){
        setLoading(true)
        val loc = "$lat,$lng"
//        val type = "hospital"
        val radius = "1500"
        val keyword = "Rumah Sakit Mata"
        api.nearbyPlace(loc, radius, keyword).enqueue(object : Callback<NearbyPlaceResponse>{
            override fun onResponse(call: Call<NearbyPlaceResponse>, response: Response<NearbyPlaceResponse>) {
                setLoading(false)
                if(response.isSuccessful){
                    places.value = response.body()?.results
                }else{
                    showToast("Error mengambil data")
                }
            }

            override fun onFailure(call: Call<NearbyPlaceResponse>, t: Throwable) {
                Log.e("MapsViewModelErr", t.message.toString())
                setLoading(false)
                showToast("Failure. Check logcat")
            }
        })
    }

    fun listenToPlaces() : LiveData<List<NearbyPlaceResponse.PlaceResponse>> = places
    fun listenToStare() : LiveData<MapsFragmentState> = state
}

sealed class MapsFragmentState {
    data class IsLoading(val isLoading: Boolean) : MapsFragmentState()
    data class ShowToast(val message: String) : MapsFragmentState()
}