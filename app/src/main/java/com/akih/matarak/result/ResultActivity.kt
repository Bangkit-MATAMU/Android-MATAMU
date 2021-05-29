package com.akih.matarak.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.databinding.ActivityResultBinding
import com.akih.matarak.util.Constants
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val SHOULD_SAVE = "save"
    }

    private lateinit var binding: ActivityResultBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        val extras = intent.extras
        extras?.let {
            val result = extras.getParcelable<DetectionResult>(EXTRA_DATA)
            val shouldSave = extras.getBoolean(SHOULD_SAVE)
            if (result != null) {
                populateItem(result)
                if (shouldSave) {
                    saveResultToDatabase(result)
                }
            }
        }
    }

    private fun saveResultToDatabase(result: DetectionResult) {
        database.child("histories").child(auth.currentUser?.uid.toString())
            .push().setValue(result)
    }

    private fun populateItem(result: DetectionResult) {
        with(binding) {
            tvMessage.text = getMessage(result.title)
            tvConfidence.text = result.confidence.toString()
            tvResult.text = result.title
            Glide.with(this@ResultActivity)
                .load(result.imageUrl)
                .into(imgInput)
        }
    }

    private fun getMessage(title: String): CharSequence =
        if (title == Constants.label[0]) {
            "Unfortunately, You are"
        } else {
          "Congratulation! You are"
        }
}