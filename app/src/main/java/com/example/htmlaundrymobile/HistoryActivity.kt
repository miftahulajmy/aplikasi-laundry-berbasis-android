package com.example.htmlaundrymobile

import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.services.TransaksiService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var customerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        customerId = sharedPreferences.getString("customerId", null)

        // Inisialisasi ViewPager dan TabLayout
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        // Set up ViewPager
        val viewPagerAdapter = ViewPagerAdapter(this, customerId)
        viewPager.adapter = viewPagerAdapter

        // Set up TabLayout with ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Dalam Proses"
                1 -> "Riwayat Pesanan"
                else -> throw IllegalStateException("Invalid position")
            }
        }.attach()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
