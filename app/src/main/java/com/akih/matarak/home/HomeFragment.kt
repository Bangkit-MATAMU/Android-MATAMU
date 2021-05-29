package com.akih.matarak.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akih.matarak.R
import com.akih.matarak.databinding.FragmentHomeBinding
import com.akih.matarak.detailarticle.DetailArticleActivity
import com.akih.matarak.history.HistoryAdapter
import com.akih.matarak.hospital.MapsViewModel
import com.akih.matarak.result.ResultActivity
import com.akih.matarak.util.Resource
import com.akih.matarak.viewmodel.ViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var viewModel: ArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory())[ArticleViewModel::class.java]

        setupRecyclerView()
        viewModel.getArticles()
        viewModel.data.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    articleAdapter.differ.submitList(response.data)
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(binding.root.context, "An error occured: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }

    private fun showErrorMessage(message: String) {
        binding.error.cvError.visibility = View.VISIBLE
        binding.error.tvErrorMessage.text = message
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideErrorMessage() {
        binding.error.cvError.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter()
        with(binding.rvArtivle) {
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }

        articleAdapter.setOnItemClickListener {
            val intent = Intent(binding.root.context, DetailArticleActivity::class.java)
            intent.putExtra(DetailArticleActivity.EXTRA_DATA, it)
            binding.root.context.startActivity(intent)
        }
    }
}