package com.akih.matarak.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.akih.matarak.data.User
import com.akih.matarak.databinding.ActivityRegisterBinding
import com.akih.matarak.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.database.reference
        setContentView(binding.root)

        binding.BtnRegis.setOnClickListener {
            val name = binding.etName.text.toString()
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val ttl = "Not yet filled"
            val alamat = "Not yet filled"
            val imageUrl = "https://firebasestorage.googleapis.com/v0/b/vaulted-arcana-312415.appspot.com/o/users%2Fperson_placeholder.png?alt=media&token=af0c3fc5-e0ee-4e03-8f6b-c7d059148211"
            val gender = 0
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        writeNewUser(username, name, imageUrl, email, ttl, alamat, gender)
                        Log.d("TAG Sukses", "createUserWithEmail:success")
                        Toast.makeText(this, "Authentication success.",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG gagal", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }

        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun writeNewUser(username: String, name: String,imageUrl: String, email: String, ttl: String, alamat: String, gender: Int) {
        val user = User(username, name, imageUrl,email, ttl, alamat, gender)
        database.child("users").child(auth.currentUser?.uid.toString()).setValue(user)
    }
}