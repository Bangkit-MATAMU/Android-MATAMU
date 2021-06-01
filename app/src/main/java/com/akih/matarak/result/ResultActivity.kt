package com.akih.matarak.result

import android.app.Activity
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.akih.matarak.data.DetectionResult
import com.akih.matarak.databinding.ActivityResultBinding
import com.akih.matarak.main.Classifier
import com.akih.matarak.util.CATARACT
import com.akih.matarak.util.Utils
import com.akih.matarak.util.Utils.toString
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val BITMAP_DATA = "bitmap"
    }

    private val mInputSize = 224
    private val mModelPath = "ModelFix.tflite"
    private val mLabelPath = "label.txt"
    private lateinit var classifier: Classifier
    private lateinit var binding: ActivityResultBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var mStorageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference
        mStorageRef = FirebaseStorage.getInstance().getReference("histories")
        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        val extras = intent.extras
        extras?.let {
            val result = extras.getParcelable<DetectionResult>(EXTRA_DATA)
            val bitmap = extras.getParcelable<Bitmap>(BITMAP_DATA)
            if (result != null) {
                populateItem(result)
            }
            if (bitmap != null) {
                uploadImage(bitmap)
            }
        }

        binding.btnGoesToHospital.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        showOnProgressState()
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
                detectImage(bitmap, task.result.toString())
            }
        }
    }

    private fun showOnProgressState() {
        binding.cvProgress.visibility = View.VISIBLE
    }

    private fun detectImage(bitmap: Bitmap, downloadUri: String) {
        val detectionResult = classifier.recognizeImage(bitmap)
        val confidence = detectionResult[0].confidence * 100
        val resultData = DetectionResult(
            "",
            downloadUri,
            detectionResult[0].title,
            Utils.getCurrentDateTime().toString("yyyyMMdd-HH:mm:ss"),
            confidence.toInt()
        )
        populateItem(resultData)
        hideOnProgressState()
        saveResultToDatabase(resultData)
    }

    private fun hideOnProgressState() {
        binding.cvProgress.visibility = View.INVISIBLE
    }

    private fun saveResultToDatabase(result: DetectionResult) {
        database.child("histories").child(auth.currentUser?.uid.toString())
            .push().setValue(result)
    }

    private fun populateItem(result: DetectionResult) {
        with(binding) {
            tvMessage.text = getMessage(result.title)
            tvConfidence.text = result.confidence.toString()
            tvResult.text = result.title
            Glide.with(this@ResultActivity)
                .load(result.imageUrl)
                .into(imgInput)
        }
    }

    private fun getMessage(title: String): CharSequence =
        if (title == CATARACT) {
            "Unfortunately, You are"
        } else {
          "Congratulation! You are"
        }
}