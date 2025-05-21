package com.example.htmlaundrymobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.adapter.TransaksiAdapter
import com.example.htmlaundrymobile.api.model.Transaksi
import com.example.htmlaundrymobile.api.services.TransaksiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class PendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transaksiAdapter: TransaksiAdapter
    private lateinit var transaksiService: TransaksiService
    private lateinit var customerId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending, container, false)

        // Mengambil ID pelanggan yang login dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        customerId = sharedPreferences.getString("customerId", "") ?: ""

        recyclerView = view.findViewById(R.id.recyclerViewPending)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        transaksiService = ApiClient.retrofit.create(TransaksiService::class.java)

        // Inisialisasi adapter dengan list kosong
        transaksiAdapter = TransaksiAdapter(emptyList(), { transaksi ->
            transaksiOnClick(transaksi)
        })
        recyclerView.adapter = transaksiAdapter

        getData()
        scheduleTransactionCheck()

        return view
    }

    private fun transaksiOnClick(transaksi: Transaksi) {
        Toast.makeText(requireContext(), transaksi.keluhan, Toast.LENGTH_SHORT).show()
    }

    private fun getData() {
        val call = transaksiService.getByCustomerId(customerId)
        call.enqueue(object : Callback<List<Transaksi>> {
            override fun onResponse(call: Call<List<Transaksi>>, response: Response<List<Transaksi>>) {
                if (response.isSuccessful) {
                    val transaksiList = response.body() ?: emptyList()
                    val pendingList = transaksiList.filter { it.status != "Diambil" }
                    transaksiAdapter.updateData(pendingList)
                } else {
                    Log.e("PendingFragment", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaksi>>, t: Throwable) {
                Log.e("PendingFragment", "Failure: ${t.localizedMessage}", t)
                Toast.makeText(requireContext(), "Request failed: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun scheduleTransactionCheck() {
        val workRequest = PeriodicWorkRequestBuilder<TransactionCheckWorker>(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "TransactionCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
