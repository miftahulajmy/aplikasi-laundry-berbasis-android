package com.example.htmlaundrymobile.api.services

import com.example.htmlaundrymobile.api.model.OrderRequest
import com.example.htmlaundrymobile.api.model.OrderResponse
import com.example.htmlaundrymobile.api.model.Pelanggan
import com.example.htmlaundrymobile.api.model.Transaksi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("API/transaksi/create/")
    fun createOrder(@Body orderRequest: OrderRequest): Call<OrderResponse>
    @GET("API/transaksi/customer/{id_pelanggan}/")
    fun getTransaksi(@Path("id_pelanggan") customerId: String): Call<List<Transaksi>>

}
