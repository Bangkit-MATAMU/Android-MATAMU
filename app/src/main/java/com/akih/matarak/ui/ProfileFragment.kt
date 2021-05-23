package com.akih.matarak.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akih.matarak.R
import com.akih.matarak.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        logout()
        return binding.root
    }

    private fun logout(){
        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(binding.root.context, LoginActivity::class.java))
        }
    }
}