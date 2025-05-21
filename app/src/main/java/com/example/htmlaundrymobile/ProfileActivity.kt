package com.example.htmlaundrymobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Ambil data pelanggan dari SharedPreferences
        val sharedPref: SharedPreferences = getSharedPreferences("PelangganPref", Context.MODE_PRIVATE)
        val nama = sharedPref.getString("nama", "Nama tidak tersedia")
        val no_hp = sharedPref.getString("no_hp", "No HP tidak tersedia")
        val alamat = sharedPref.getString("alamat", "Alamat tidak tersedia")
        val username = sharedPref.getString("username", "Username tidak tersedia")

        // Mengisi TextView dengan data pelanggan
        findViewById<TextView>(R.id.nama).text = nama
        findViewById<TextView>(R.id.no_hp).text = no_hp
        findViewById<TextView>(R.id.alamat).text = alamat
        findViewById<TextView>(R.id.username).text = username
    }
}
