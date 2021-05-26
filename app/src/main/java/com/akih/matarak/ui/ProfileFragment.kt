package com.akih.matarak.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akih.matarak.R
import com.akih.matarak.data.User
import com.akih.matarak.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        database = Firebase.database.reference

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(binding.root.context, LoginActivity::class.java))
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(binding.root.context, EditProfileActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadDataProfile()
    }

    private fun loadDataProfile(){
        database.child("users").child(auth.currentUser?.uid.toString()).get().addOnSuccessListener {user ->
            binding.apply {
                tvProfileName.text = user.child("name").value.toString()
                tvProfileEmail.text = user.child("email").value.toString()
                tvProfileBirthDate.text = user.child("ttl").value.toString()
                tvProfileLocation.text = user.child("alamat").value.toString()
                if (user.child("gender").value.toString() == "0"){
                    tvProfileGender.text = "Male"
                } else {
                    tvProfileGender.text = "Female"
                }

            }
            Log.i("firebase", ""+user.value+"")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
}