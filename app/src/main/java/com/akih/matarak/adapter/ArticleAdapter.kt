package com.akih.matarak.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akih.matarak.data.ModelArticle
import com.akih.matarak.databinding.ItemListArticleBinding
import com.bumptech.glide.Glide

class ArticleAdapter(private val article: List<ModelArticle>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(){
    inner class ArticleViewHolder(val binding: ItemListArticleBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(ItemListArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = article.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.binding.apply {
            tvTitle.text = article[position].title
            tvContent.text = article[position].content
        }

    }
}