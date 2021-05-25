package com.akih.matarak.ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.akih.matarak.R
import com.akih.matarak.databinding.ActivityEditProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var cal = Calendar.getInstance()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        database = Firebase.database.reference
        setContentView(binding.root)

        setupDatePicker()
        setupSpinner()

        binding.btnSave.setOnClickListener {
            val name = binding.etProfileName.text
            val email = binding.etProfileEmail.text
            val birthdate = binding.tvBirthDate.text
            val location = binding.etProfileLocation.text
            val gender = binding.spinnerGender.selectedItem as String

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
}