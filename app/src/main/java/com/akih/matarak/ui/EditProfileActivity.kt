package com.akih.matarak.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import com.akih.matarak.R
import com.akih.matarak.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var cal = Calendar.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    internal lateinit var mStrageRef: StorageReference
    private lateinit var imagePath : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.database.reference
        mStrageRef = FirebaseStorage.getInstance().getReference("users")

        setContentView(binding.root)
        loadDataEditProfile()
        setupDatePicker()
        setupSpinner()

        binding.personPlaceholder.setOnClickListener {
            openGalleryForImage()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etProfileName.text
            val email = binding.etProfileEmail.text
            val birthdate = binding.tvBirthDate.text
            val location = binding.etProfileLocation.text
            val gender = binding.spinnerGender.selectedItem as String
            binding.personPlaceholder.setImageURI(imagePath)

            Log.d("edit", "onCreate: $name $email $birthdate $location $gender")

            onBackPressed()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Profile"
    }

    private fun setupDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.btnBirthDate.setOnClickListener {
            DatePickerDialog(this@EditProfileActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvBirthDate.text = sdf.format(cal.time)
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(this,
            R.layout.dropdown_list, resources.getStringArray(R.array.gender))
        binding.spinnerGender.adapter = spinnerAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDataEditProfile(){
        database.child("users").child(auth.currentUser?.uid.toString()).get().addOnSuccessListener {user ->
            val gender = user.child("gender").value.toString()
            binding.apply {
                etProfileName.setText(user.child("name").value.toString())
                etProfileEmail.setText(user.child("email").value.toString())
                tvBirthDate.text = user.child("ttl").value.toString()
                etProfileLocation.setText(user.child("alamat").value.toString())
                spinnerGender.setSelection(gender.toInt())
            }
            Log.i("firebase", ""+user.value+"")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun openGalleryForImage() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data
            Log.d("KUNYUK", imageUri.toString())
            binding.personPlaceholder.setImageURI(imageUri)
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri?) {
        val Ref = mStrageRef.child(System.currentTimeMillis().toString() + "." + getExtension(imageUri))
        Ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    imagePath = it.uploadSessionUri!!
                    Log.d("DATA KINTIL", imagePath.toString())
                    Toast.makeText(this@EditProfileActivity, "Image uploaded successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    // Handle unsuccessful uploads
                    // ...
                    Log.d("KONTOLL", it.message.toString())
                }
    }

    private fun getExtension(uri: Uri?): String? {
        val cr = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri!!))
    }
}