package com.example.htmlaundrymobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.adapter.LayananAdapter
import com.example.htmlaundrymobile.api.model.JenisLayanan
import com.example.htmlaundrymobile.api.model.JenisLayananResponse
import com.example.htmlaundrymobile.api.services.LayananService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LayananActivity : AppCompatActivity() {

    private lateinit var apiService: LayananService
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LayananAdapter
    private var jenisLayananList = mutableListOf<JenisLayanan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan)
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        // Enable the home button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.layananList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LayananAdapter(jenisLayananList)
        recyclerView.adapter = adapter

        // Initialize Retrofit service
        apiService = ApiClient.retrofit.create(LayananService::class.java)

        // Fetch data from API
        fetchJenisLayanan()
    }

    private fun fetchJenisLayanan() {
        apiService.getJenisLayanan().enqueue(object : Callback<JenisLayananResponse> {
            override fun onResponse(call: Call<JenisLayananResponse>, response: Response<JenisLayananResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { jenisLayananResponse ->
                        jenisLayananList.clear()
                        jenisLayananList.addAll(jenisLayananResponse.data_jenislayanan)
                        Log.d("LayananActivity", "Fetched data: $jenisLayananList")
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@LayananActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JenisLayananResponse>, t: Throwable) {
                Log.e("LayananActivity", "onFailure: ${t.message}")
                Toast.makeText(this@LayananActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
