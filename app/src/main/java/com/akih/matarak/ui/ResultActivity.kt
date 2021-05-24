package com.akih.matarak.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.databinding.ActivityResultBinding
import com.akih.matarak.util.Constants
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        extras?.let {
            val result = extras.getParcelable<DetectionResult>(EXTRA_DATA)
            if (result != null) {
                populateItem(result)
            }
        }
    }

    private fun populateItem(result: DetectionResult) {
        with(binding) {
            tvMessage.text = getMessage(result.title)
            tvConfidence.text = result.confidence.toString()
            tvResult.text = result.title
            Glide.with(this@ResultActivity)
                .load(result.image)
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