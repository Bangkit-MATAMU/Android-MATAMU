package com.akih.matarak.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akih.matarak.databinding.ItemBannerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BannerAdapter: RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: ItemBannerBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem.equals(newItem)

        override fun areContentsTheSame(oldUrl: String, newUrl: String) =
            oldUrl.equals(newUrl)
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val itemBinding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = differ.currentList[position]
        Glide.with(holder.itemView.context)
            .load(banner)
            .apply(RequestOptions().override(320,180))
            .into(holder.binding.imgBanner)
    }

    override fun getItemCount() = differ.currentList.size
}