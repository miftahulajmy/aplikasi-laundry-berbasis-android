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
import com.example.htmlaundrymobile.api.model.LoginRequest
import com.example.htmlaundrymobile.api.model.LoginResponse
import com.example.htmlaundrymobile.api.services.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var isActivityActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)
        val textRegister: TextView = findViewById(R.id.buttonRegister)

        if (authToken != null) {
            // Token ditemukan, navigasi langsung ke Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Token tidak ditemukan, tampilkan layar login
            setContentView(R.layout.activity_login)

            val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
            val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
            val loginButton = findViewById<Button>(R.id.buttonLogin)
            val textRegister: TextView = findViewById(R.id.buttonRegister)

            // Menambahkan OnClickListener
            textRegister.setOnClickListener {
                // Lakukan aksi yang diinginkan saat teks "Daftar" diklik
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
            }

            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                var isValid = true

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
                    Log.d("LoginActivity", "Attempting to log in with username: $username")
                    performLogin(username, password)
                } else {
                    Toast.makeText(this, "Lengkapi data diatas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        val loginService = ApiClient.retrofit.create(LoginService::class.java)
        val loginRequest = LoginRequest(username, password)

        loginService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.token != null) {
                        // Simpan token dan nama ke SharedPreferences
                        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", loginResponse.token)
                        editor.putString("customerName", loginResponse.nama) // Simpan nama pelanggan
                        editor.putString("customerId", loginResponse.id_pelanggan.toString()) // Simpan ID pelanggan
                        editor.apply()

                        Log.d("LoginActivity", "Token saved: ${loginResponse.token}")

                        // Berhasil login, navigasi ke Dashboard
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("LoginActivity", "Login failed: token is null")
                        showAlert("Login Failed", "Invalid response from server")
                    }
                } else {
                    // Gagal login
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginActivity", "Login failed: $errorBody")

                    if (response.code() == 401) {
                        showAlert("Login Failed", "Invalid password")
                    } else if (response.code() == 404) {
                        showAlert("Login Failed", "User not found")
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Gagal mengirim permintaan
                t.printStackTrace()
                showAlert("Login Failed", "Request failed: ${t.localizedMessage}")
            }
        })
    }

    private fun showAlert(title: String, message: String) {
        if (isActivityActive) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityActive = false
    }
}
