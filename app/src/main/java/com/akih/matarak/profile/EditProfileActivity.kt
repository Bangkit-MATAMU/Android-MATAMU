package com.akih.matarak.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.akih.matarak.R
import com.akih.matarak.data.User
import com.akih.matarak.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var cal = Calendar.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    internal lateinit var mStrageRef: StorageReference
    private lateinit var imagePath : String
    private lateinit var usernameTemp: String

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
            val username = usernameTemp
            val name = binding.etProfileName.text.toString()
            val email = binding.etProfileEmail.text.toString()
            val birthdate = binding.tvBirthDate.text.toString()
            val location = binding.etProfileLocation.text.toString()
            val gender = binding.spinnerGender.selectedItemPosition
            val imageUrl = imagePath

            writeNewUsers(username, name, imageUrl, email, birthdate, location, gender)

            Log.d("edit", "onCreate: $username $name $email $birthdate $location $gender $imageUrl")

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
            usernameTemp = user.child("username").value.toString()
            imagePath = user.child("imageUrl").value.toString()
            binding.apply {
                Glide.with(this@EditProfileActivity)
                        .load(user.child("imageUrl").value.toString())
                        .into(binding.personPlaceholder)
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
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data
            binding.personPlaceholder.setImageURI(imageUri)
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Any?) {
        val ref = mStrageRef.child(System.currentTimeMillis().toString())
        val uploadTask: UploadTask = ref.putFile((imageUri as Uri?)!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnSuccessListener {
            Toast.makeText(this@EditProfileActivity, "Image uploaded successfully", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this@EditProfileActivity, "Image uploaded failed", Toast.LENGTH_LONG).show()
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imagePath = task.result.toString()
                Toast.makeText(this@EditProfileActivity, "Image uploaded done", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun writeNewUsers(username: String, name: String,imageUrl: String, email: String, ttl: String, alamat: String, gender: Int) {
        val key = database.child("users").push().key
        if (key == null) {
            Log.w("TAG", "Couldn't get push key for posts")
            return
        }

        val post = User(username, name, imageUrl, email, ttl, alamat, gender)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>(
                "/users/${auth.currentUser?.uid}/" to postValues
        )

        database.updateChildren(childUpdates)
    }
}