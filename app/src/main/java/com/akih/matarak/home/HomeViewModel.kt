package com.akih.matarak.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akih.matarak.data.Article
import com.akih.matarak.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class HomeViewModel: ViewModel() {

    private val database: DatabaseReference = Firebase.database.reference
    val data: MutableLiveData<Resource<List<Article>>> = MutableLiveData()
    val banner: MutableLiveData<Resource<List<String>>> = MutableLiveData()

    fun getArticles() {
        data.postValue(Resource.Loading())
        val articles: MutableList<Article> = mutableListOf()
        val ref = database.child("articles")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (articleSnapshot in dataSnapshot.children) {
                    val article = articleSnapshot.getValue<Article>()
                    if (article != null) {
                        articles.add(article)
                    }
                }
                data.postValue(Resource.Success(articles))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                data.postValue(Resource.Error(databaseError.message))
            }
        })
    }

    fun getBanners() {
        banner.postValue(Resource.Loading())
        val ref = database.child("banners")
        val banners: MutableList<String> = mutableListOf()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (bannerSnapshot in dataSnapshot.children) {
                    val bannerData = bannerSnapshot.getValue<String>()
                    if (bannerData != null) {
                        banners.add(bannerData)
                    }
                }
                banner.postValue(Resource.Success(banners))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                banner.postValue(Resource.Error(databaseError.message))
            }
        })
    }

}