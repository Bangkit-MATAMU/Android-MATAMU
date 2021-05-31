package com.akih.matarak.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
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
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity(), SetFragmentChange {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    private lateinit var binding: ActivityMainBinding
    private val mInputSize = 224
    private val mModelPath = "ModelFix.tflite"
    private val mLabelPath = "label.txt"
    private lateinit var classifier: Classifier
    private lateinit var mStorageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
        mStorageRef = FirebaseStorage.getInstance().getReference("histories")

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
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val bitmap = data?.extras?.get("data") as Bitmap
                val result = classifier.recognizeImage(bitmap)
                uploadImage(bitmap, result)
            }
        }else if(resultCode == Activity.RESULT_OK && requestCode == 666){
            Toast.makeText(applicationContext, "pindah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(bitmap: Bitmap, result: List<Classifier.Recognition>) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        val ref = mStorageRef.child(System.currentTimeMillis().toString())
        val uploadTask: UploadTask = ref.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                goToResultActivity(task.result.toString(), result)
            }
        }
    }

    private fun goToResultActivity(downloadUri: String, result: List<Classifier.Recognition>) {
        val confidence = result[0].confidence * 100
        val detectionResult = DetectionResult(
            "",
            downloadUri,
            result[0].title,
            getCurrentDateTime().toString("yyyyMMdd-HH:mm:ss"),
            confidence.toInt()
        )

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_DATA, detectionResult)
        intent.putExtra(ResultActivity.SHOULD_SAVE, true)
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