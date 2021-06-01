package com.akih.matarak.history

import android.app.Activity.RESULT_OK
import android.content.Context
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
import com.akih.matarak.databinding.FragmentHistoryBinding
import com.akih.matarak.hospital.HospitalFragment
import com.akih.matarak.main.SetFragmentChange
import com.akih.matarak.result.ResultActivity
import com.akih.matarak.util.Resource

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private var setFragmentChangeListener: SetFragmentChange? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        setFragmentChangeListener = context as SetFragmentChange

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory())[HistoryViewModel::class.java]
        setupRecyclerView()

        viewModel.getHistoriesData()
        viewModel.data.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    historyAdapter.differ.submitList(response.data)
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
        historyAdapter = HistoryAdapter()
        with(binding.rvHistory) {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        historyAdapter.setOnItemClickListener {
            val intent = Intent(binding.root.context, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_DATA, it)
            startActivityForResult(intent, 666)
//            binding.root.context.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 666){
            setFragmentChangeListener?.changeFragmentTo(HospitalFragment())
        }
    }
}