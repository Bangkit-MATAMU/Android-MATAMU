package com.akih.matarak.ui

import android.Manifest
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.akih.matarak.R
import com.akih.matarak.databinding.ActivityMainBinding
import com.github.florent37.runtimepermission.kotlin.askPermission

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
        askPermissionGPS()
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

    private fun askPermissionGPS(){
        askPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ){

        }.onDeclined{e ->
            if (e.hasDenied()){
                e.denied.forEach{

                }

                AlertDialog.Builder(this)
                    .setMessage("Please Accept Our Permission")
                    .setPositiveButton("Yes"){_,_ ->
                        e.askAgain()
                    }
                    .setNegativeButton("No"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if (e.hasForeverDenied()){
                e.foreverDenied.forEach {

                }
                e.goToSettings()
            }
        }
    }
}