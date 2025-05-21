package com.example.htmlaundrymobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htmlaundrymobile.api.ApiClient
import com.example.htmlaundrymobile.api.adapter.TransaksiAdapter
import com.example.htmlaundrymobile.api.model.Transaksi
import com.example.htmlaundrymobile.api.services.TransaksiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompletedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transaksiAdapter: TransaksiAdapter
    private lateinit var transaksiService: TransaksiService
    private lateinit var customerId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed, container, false)

        // Mengambil ID pelanggan yang login dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        customerId = sharedPreferences.getString("customerId", "") ?: ""

        recyclerView = view.findViewById(R.id.recyclerViewCompleted)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        transaksiService = ApiClient.retrofit.create(TransaksiService::class.java)

        // Inisialisasi adapter dengan list kosong
        transaksiAdapter = TransaksiAdapter(emptyList(), { transaksi ->
            transaksiOnClick(transaksi)
        }, { idTransaksi, keluhan, keluhanInput ->
            updateKeluhan(idTransaksi, keluhan, keluhanInput)
        })
        recyclerView.adapter = transaksiAdapter

        getData()

        return view
    }

    private fun transaksiOnClick(transaksi: Transaksi) {
        Toast.makeText(requireContext(), transaksi.keluhan, Toast.LENGTH_SHORT).show()
    }

    private fun updateKeluhan(idTransaksi: Int, keluhan: String, keluhanInput: EditText) {
        val keluhanMap = mapOf("keluhan" to keluhan)
        transaksiService.updateKeluhan(idTransaksi, keluhanMap).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Keluhan berhasil diupdate", Toast.LENGTH_SHORT).show()
                    keluhanInput.text.clear() // Clear the input field after successful update
                    getData() // Refresh the data
                } else {
                    Log.e("CompletedFragment", "Failed to update complaint, response code: ${response.code()}")
                    Toast.makeText(requireContext(), "Gagal mengupdate keluhan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("CompletedFragment", "Error updating complaint: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getData() {
        val call = transaksiService.getByCustomerId(customerId)
        call.enqueue(object : Callback<List<Transaksi>> {
            override fun onResponse(call: Call<List<Transaksi>>, response: Response<List<Transaksi>>) {
                if (response.isSuccessful) {
                    val transaksiList = response.body() ?: emptyList()
                    val completedList = transaksiList.filter { it.status == "Diambil" }
                    transaksiAdapter.updateData(completedList)
                } else {
                    Log.e("CompletedFragment", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaksi>>, t: Throwable) {
                Log.e("CompletedFragment", "Failure: ${t.localizedMessage}", t)
                Toast.makeText(requireContext(), "Request failed: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
