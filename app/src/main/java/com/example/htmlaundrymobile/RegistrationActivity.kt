package com.example.htmlaundrymobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.model.RegisterResponse
import com.example.htmlaundrymobile.api.services.RegisterService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val addressEditText = findViewById<EditText>(R.id.editTextAdress)
        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val textLogin: TextView = findViewById(R.id.textLogin)

        textLogin.setOnClickListener {
            // Aksi yang diinginkan saat teks "Login" diklik
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonRegister).setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            var isValid = true

            if (name.isEmpty()) {
                nameEditText.error = "Name is required"
                isValid = false
            }
            if (phone.isEmpty()) {
                phoneEditText.error = "Phone number is required"
                isValid = false
            }
            if (address.isEmpty()) {
                addressEditText.error = "Address is required"
                isValid = false
            }
            if (username.isEmpty()) {
                usernameEditText.error = "Username is required"
                isValid = false
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordEditText.error = "Password must be at least 6 characters"
                isValid = false
            }

            if (isValid) {
                performRegistration(name, phone, address, username, password)
            } else {
                Toast.makeText(this, "Please correct the errors above", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performRegistration(name: String, phone: String, address: String, username: String, password: String) {
        val registerService = ApiClient.retrofit.create(RegisterService::class.java)

        val namaPart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val noHpPart = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
        val alamatPart = RequestBody.create("text/plain".toMediaTypeOrNull(), address)
        val usernamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), username)
        val passwordPart = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

        val call = registerService.register(namaPart, noHpPart, alamatPart, usernamePart, passwordPart)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("performRegistration", "Response Code: ${response.code()}")
                Log.d("performRegistration", "Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse?.token != null) {
                        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putString("auth_token", registerResponse.token)
                            putString("customerName", registerResponse.nama)
                            putString("customerId", registerResponse.id_pelanggan.toString())
                            apply()
                        }
                        Log.d("RegistrationActivity", "Registration successful: ${registerResponse.token}")
                        showSuccessAlert("Registration Successful", "You have been successfully registered. Please log in.")
                    } else {
                        Log.e("RegistrationActivity", "Registration failed: token is null")
                        showAlert("Registration Failed", "Invalid response from server")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegistrationActivity", "Registration failed: $errorBody")
                    showAlert("Registration Failed", "Failed to register: $errorBody")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                t.printStackTrace()
                showAlert("Registration Failed", "Request failed: ${t.localizedMessage}")
            }
        })
    }

    private fun showSuccessAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
