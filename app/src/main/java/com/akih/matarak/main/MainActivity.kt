package com.akih.matarak.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.akih.matarak.R
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.databinding.ActivityMainBinding
import com.akih.matarak.history.HistoryFragment
import com.akih.matarak.home.HomeFragment
import com.akih.matarak.hospital.HospitalFragment
import com.akih.matarak.profile.ProfileFragment
import com.akih.matarak.result.ResultActivity
import com.akih.matarak.util.Utils.getCurrentDateTime
import com.akih.matarak.util.Utils.toString
import com.canhub.cropper.CropImage
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity(), SetFragmentChange {

    companion object {
        private const val CAMERA_REQUEST_CODE = 2
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val hospitalFragment = HospitalFragment()
        val historyFragment = HistoryFragment()
        val profileFragment = ProfileFragment()
        askPermissions()
        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miHospital -> setCurrentFragment(hospitalFragment)
                R.id.miHistory -> setCurrentFragment(historyFragment)
                R.id.miProfile -> setCurrentFragment(profileFragment)
            }
            true
        }

        binding.fab.setOnClickListener {
            Pix.start(this@MainActivity,
                Options.init()
                    .setRequestCode(CAMERA_REQUEST_CODE)
                    .setMode(Options.Mode.Picture))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val returnValue: ArrayList<String> = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS) as ArrayList<String>
                CropImage.activity(Uri.fromFile(File(returnValue[0])))
                    .start(this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (result != null) {
                    val file = File(result.getUriFilePath(this@MainActivity)!!)
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    goToResultActivity(bitmap)
                }
            }
        }else if(resultCode == Activity.RESULT_OK && requestCode == 666){
            Toast.makeText(applicationContext, "pindah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToResultActivity(bitmap: Bitmap) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.BITMAP_DATA, bitmap)
        startActivity(intent)
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun askPermissions(){
        askPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        ){

        }.onDeclined{ e ->
            if (e.hasDenied()){
                e.denied.forEach{

                }

                AlertDialog.Builder(this)
                    .setMessage("Please Accept Our Permission")
                    .setPositiveButton("Yes"){ _, _ ->
                        e.askAgain()
                    }
                    .setNegativeButton("No"){ dialog, _ ->
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

    override fun changeFragmentTo(fragment: Fragment) {
        setCurrentFragment(fragment)
    }


}