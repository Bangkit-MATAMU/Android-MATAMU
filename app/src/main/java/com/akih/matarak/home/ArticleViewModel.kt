package com.akih.matarak.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akih.matarak.data.Article
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

class ArticleViewModel: ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    val data: MutableLiveData<Resource<List<Article>>> = MutableLiveData()

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
                Log.d("coba", "onDataChange: ${articles.size}")
                Log.d("coba", "onDataChange: ${dataSnapshot}")
                Log.d("coba", "onDataChange: ${dataSnapshot.children}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("error", "loadPost:onCancelled", databaseError.toException())
                data.postValue(Resource.Error(databaseError.message))
            }
        })
    }

}