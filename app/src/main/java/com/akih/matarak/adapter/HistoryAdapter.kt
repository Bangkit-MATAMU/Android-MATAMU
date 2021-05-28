package com.akih.matarak.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.databinding.ItemHistoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.DetectionResultViewHolder>() {

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<DetectionResult>() {
            override fun areItemsTheSame(oldItem: DetectionResult, newItem: DetectionResult) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldResult: DetectionResult, newResult: DetectionResult) =
                oldResult == newResult
        }
    }

    inner class DetectionResultViewHolder(val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root)

    val differ = AsyncListDiffer(this, differCallback)
    private var onItemClickListener: ((DetectionResult) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectionResultViewHolder {
        val itemBinding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetectionResultViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DetectionResultViewHolder, position: Int) {
        val history = differ.currentList[position]

        holder.binding.tvHistoryTime.text = history.time
        holder.binding.tvHistoryConfidence.text = history.confidence.toString()
        holder.binding.tvHistoryTitle.text = history.title
        Glide.with(holder.itemView.context)
            .load(history.imageUrl)
            .apply(RequestOptions().override(75,100))
            .into(holder.binding.imgHistory)

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(history) }
        }
    }

    override fun getItemCount() = differ.currentList.size

    fun setOnItemClickListener(listener: (DetectionResult) -> Unit) {
        onItemClickListener = listener
    }
}