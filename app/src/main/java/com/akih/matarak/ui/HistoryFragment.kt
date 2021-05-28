package com.akih.matarak.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.akih.matarak.R
import com.akih.matarak.adapter.HistoryAdapter
import com.akih.matarak.databinding.FragmentHistoryBinding
import com.akih.matarak.util.Resource
import com.akih.matarak.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = HistoryViewModel()
        setupRecyclerView()

        viewModel.getHistoriesData()
        viewModel.data.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    historyAdapter.differ.submitList(response.data)
                }
                // perlu tambahkan resource loading dan error
            }
        })
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        with(binding.rvHistory) {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        historyAdapter.setOnItemClickListener {
            val intent = Intent(binding.root.context, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_DATA, it)
            intent.putExtra(ResultActivity.SHOULD_SAVE, false)
            binding.root.context.startActivity(intent)
        }
    }
}