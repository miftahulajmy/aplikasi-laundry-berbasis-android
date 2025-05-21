package com.example.htmlaundrymobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.model.OrderRequest
import com.example.htmlaundrymobile.api.model.OrderResponse
import com.example.htmlaundrymobile.api.model.Transaksi
import com.example.htmlaundrymobile.api.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val alertSelesai = findViewById<View>(R.id.alertSelesai)
        val alertTerkirim = findViewById<View>(R.id.alertProses)

        // Default: hide alert
        alertSelesai.visibility = View.GONE
        alertTerkirim.visibility = View.GONE

        // Fetch and check transactions
        checkTransactions(alertSelesai, alertTerkirim)

        // Initialize Retrofit service
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Get customer name and id from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val customerName = sharedPreferences.getString("customerName", "Guest")
        val customerId = sharedPreferences.getString("customerId", "")

        // Display customer name in TextView
        val welcomeTextView = findViewById<TextView>(R.id.textViewWelcome)
        welcomeTextView.text = "$customerName"

        findViewById<CardView>(R.id.cardLogout).setOnClickListener {
            showLogoutConfirmationDialog()
        }

        findViewById<CardView>(R.id.cardHistory).setOnClickListener {
            history()
        }

        findViewById<CardView>(R.id.cardOrder).setOnClickListener {
            customerId?.let {
                showOrderConfirmationDialog(it)
            } ?: Toast.makeText(this, "Customer ID not found", Toast.LENGTH_SHORT).show()
        }

        findViewById<CardView>(R.id.cardLayanan).setOnClickListener {
            layanan()
        }

        findViewById<Button>(R.id.closeSelesai).setOnClickListener {
            closeAlert(alertSelesai)
        }

        findViewById<Button>(R.id.closeProses).setOnClickListener {
            closeAlert(alertTerkirim)
        }
    }

    private fun closeAlert(alertView: View) {
        alertView.visibility = View.GONE
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_alert_logout, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.buttonYes).setOnClickListener {
            alertDialog.dismiss()
            logout()
        }

        dialogView.findViewById<Button>(R.id.buttonNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()

        // Return to login screen
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun history() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showOrderConfirmationDialog(customerId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_alert_order, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.buttonYes).setOnClickListener {
            alertDialog.dismiss()
            makeOrderRequest(customerId)
        }

        dialogView.findViewById<Button>(R.id.buttonNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun makeOrderRequest(customerId: String) {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)
        val call = apiService.getTransaksi(customerId)

        call.enqueue(object : Callback<List<Transaksi>> {
            override fun onResponse(call: Call<List<Transaksi>>, response: Response<List<Transaksi>>) {
                if (response.isSuccessful) {
                    val transaksiList = response.body()
                    if (transaksiList != null) {
                        val hasPending = transaksiList.any { it.status.trim().lowercase() == "pending" }
                        if (hasPending) {
                            Toast.makeText(this@DashboardActivity, "Tidak bisa melakukan pemesanan baru karena masih ada transaksi yang pending.", Toast.LENGTH_LONG).show()
                        } else {
                            // Lanjutkan dengan pemesanan
                            performOrderRequest(customerId)
                        }
                    } else {
                        // No transactions found, allow new order
                        performOrderRequest(customerId)
                    }
                } else {
                    if (response.code() == 404) {
                        // No transactions found, allow new order
                        performOrderRequest(customerId)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@DashboardActivity, "Gagal mengambil data transaksi: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Transaksi>>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Gagal mengambil data transaksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun performOrderRequest(customerId: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Hitung tanggal selesai
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(currentDate) ?: Date()
        calendar.add(Calendar.DAY_OF_YEAR, 3)
        val tanggalSelesai = dateFormat.format(calendar.time)

        // Buat OrderRequest dengan tanggal selesai
        val orderRequest = OrderRequest(
            idPelanggan = customerId,
            tanggalMasuk = currentDate,
            tanggalSelesai = tanggalSelesai
        )

        apiService.createOrder(orderRequest).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(this@DashboardActivity, it.message, Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(this@DashboardActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@DashboardActivity, "Failed to place order: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Failed to place order: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun layanan() {
        val intent = Intent(this, LayananActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkTransactions(alertSelesai: View, alertTerkirim: View) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val customerId = sharedPreferences.getString("customerId", "")

        Log.d("DashboardActivity", "Customer ID: $customerId") // Log the customer ID

        if (customerId.isNullOrEmpty()) {
            Log.e("DashboardActivity", "Customer ID is null or empty")
            Toast.makeText(this, "Customer ID is not available", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.retrofit.create(ApiService::class.java)
        val call = apiService.getTransaksi(customerId)

        call.enqueue(object : Callback<List<Transaksi>> {
            override fun onResponse(call: Call<List<Transaksi>>, response: Response<List<Transaksi>>) {
                if (response.isSuccessful) {
                    val transaksiList = response.body()
                    Log.d("DashboardActivity", "Transaksi List: $transaksiList") // Log the transaction list

                    if (transaksiList != null) {
                        transaksiList.forEach { transaksi ->
                            Log.d("DashboardActivity", "Transaksi Status: ${transaksi.status}")
                        }

                        val hasSelesai = transaksiList.any { it.status.trim().lowercase() == "selesai" }
                        val hasPending = transaksiList.any { it.status.trim().lowercase() == "pending" }

                        Log.d("DashboardActivity", "Has Selesai: $hasSelesai") // Log status check
                        Log.d("DashboardActivity", "Has Pending: $hasPending") // Log status check

                        alertSelesai.visibility = if (hasSelesai) View.VISIBLE else View.GONE
                        alertTerkirim.visibility = if (hasPending) View.VISIBLE else View.GONE

                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DashboardActivity", "Failed to retrieve transactions: $errorBody")
                    Toast.makeText(this@DashboardActivity, "Failed to retrieve transactions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaksi>>, t: Throwable) {
                Log.e("DashboardActivity", "Error: ${t.message}")
                Toast.makeText(this@DashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
