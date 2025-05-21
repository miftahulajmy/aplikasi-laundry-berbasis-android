package com.example.htmlaundrymobile.api.services

import com.example.htmlaundrymobile.api.model.Transaksi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransaksiService {
    @GET("API/transaksi")
    fun getAll(): Call<List<Transaksi>>

    @GET("API/transaksi/customer/{id_pelanggan}")
    fun getByCustomerId(@Path("id_pelanggan") customerId: String): Call<List<Transaksi>>

    @PUT("API/transaksi/{id_transaksi}/keluhan")
    fun updateKeluhan(@Path("id_transaksi") idTransaksi: Int, @Body keluhan: Map<String, String>): Call<Void>

    @DELETE("API/transaksi/delete/{id_transaksi}/")
    fun deleteTransaksi(@Path("id_transaksi") idTransaksi: Int): Call<List<Transaksi>>
}
