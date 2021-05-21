package com.akih.matarak.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.akih.matarak.R
import com.akih.matarak.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val hospitalFragment = HospitalFragment()
        val historyFragment = HistoryFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment, "home")

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> setCurrentFragment(homeFragment, "home")
                R.id.miHospital -> setCurrentFragment(hospitalFragment, "hospital")
                R.id.miHistory -> setCurrentFragment(historyFragment, "history")
                R.id.miProfile -> setCurrentFragment(profileFragment, "profile")
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment, nama: String) =
        supportFragmentManager.beginTransaction().apply {
            Log.d("coba", "setCurrentFragment: $nama")
            replace(R.id.flFragment, fragment)
            commit()
        }
}