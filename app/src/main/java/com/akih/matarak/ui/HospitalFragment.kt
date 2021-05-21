package com.akih.matarak.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akih.matarak.R
import com.akih.matarak.databinding.FragmentHospitalBinding

class HospitalFragment : Fragment() {

    private lateinit var binding: FragmentHospitalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}