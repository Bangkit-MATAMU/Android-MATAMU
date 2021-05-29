package com.akih.matarak.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class HistoryViewModel: ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    val data: MutableLiveData<Resource<List<DetectionResult>>> = MutableLiveData()

    fun getHistoriesData() {
        data.postValue(Resource.Loading())
        val histories: MutableList<DetectionResult> = mutableListOf()
        val ref = database.child("histories").child(auth.currentUser?.uid.toString())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (historySnapshot in dataSnapshot.children) {
                    val key = historySnapshot.key
                    val history = historySnapshot.getValue<DetectionResult>()
                    if (history != null) {
                        history.id = key ?: ""
                        histories.add(history)
                    }
                }
                data.postValue(Resource.Success(histories))
                Log.d("coba", "onDataChange: ${histories.size}")
                Log.d("coba", "onDataChange: ${histories}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException())
                data.postValue(Resource.Error(databaseError.message))
            }
        })
    }

}