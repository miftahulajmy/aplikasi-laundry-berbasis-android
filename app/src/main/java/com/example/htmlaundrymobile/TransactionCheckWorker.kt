package com.example.htmlaundrymobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.htmlaundrymobile.R
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.model.Transaksi
import com.example.htmlaundrymobile.api.services.TransaksiService
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionCheckWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val transaksiService: TransaksiService = ApiClient.retrofit.create(TransaksiService::class.java)

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val customerId = sharedPreferences.getString("customerId", "") ?: ""

        val call = transaksiService.getByCustomerId(customerId)
        val response = call.execute()

        if (response.isSuccessful) {
            val transaksiList = response.body() ?: emptyList()
            val pendingList = transaksiList.filter { it.status == "Selesai" && isCompletionDateExceeded(it) }
            pendingList.forEach { showNotification(it) }
        }

        return Result.success()
    }

    private fun isCompletionDateExceeded(transaksi: Transaksi): Boolean {
        val currentDate = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val completionDate = sdf.parse(transaksi.tanggalSelesai)?.time ?: 0

        return currentDate > completionDate
    }

    private fun showNotification(transaksi: Transaksi) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "Nama Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setSmallIcon(R.drawable.baseline_local_laundry_service_24)
            .setContentTitle("Pemberitahuan Transaksi")
            .setContentText("Transaksi tanggal ${transaksi.tanggalMasuk} telah selesai dan melewati batas waktu.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(transaksi.idTransaksi, notification)
    }
}
