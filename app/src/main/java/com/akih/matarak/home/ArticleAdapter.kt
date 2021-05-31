package com.akih.matarak.home

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akih.matarak.data.Article
import com.akih.matarak.databinding.ItemListArticleBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ArticleAdapter: RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemListArticleBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article) =
                oldItem.thumbnail == newItem.thumbnail

        override fun areContentsTheSame(oldArticle: Article, newArticle: Article) =
                oldArticle == newArticle
    }

    val differ = AsyncListDiffer(this, differCallback)
    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemBinding = ItemListArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        val content = truncateString(article.content, 80)
        holder.binding.tvTitle.text = truncateString(article.title, 25)
        holder.binding.tvContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
                                        } else {
                                                Html.fromHtml(content)
                                        }
        Glide.with(holder.itemView.context)
                .load(article.thumbnail)
                .apply(RequestOptions().override(160,120))
                .into(holder.binding.ivThumbnail)

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(article) }
        }
    }

    override fun getItemCount() = differ.currentList.size

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private fun truncateString(string: String?, maxSize: Int): String? {
        if (string != null) {
            if (string.length < maxSize) return string
            return string.take(maxSize) + "..."
        } else {
            return null
        }
    }
}