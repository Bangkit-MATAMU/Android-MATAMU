package com.akih.matarak.detailarticle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.akih.matarak.R
import com.akih.matarak.data.Article
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.databinding.ActivityDetailArticleBinding
import com.akih.matarak.result.ResultActivity
import com.bumptech.glide.Glide

class DetailArticleActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    private lateinit var binding: ActivityDetailArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        extras?.let {
            val result = extras.getParcelable<Article>(EXTRA_DATA)
            if (result != null) {
                populateItem(result)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun populateItem(result: Article) {
        with (binding) {
            tvDetailTitle.text = result.title
            tvDetailContent.text = result.content
            tvDetailSource.text = result.source
            Glide.with(this@DetailArticleActivity)
                .load(result.thumbnail)
                .into(imgDetailThumbnail)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}