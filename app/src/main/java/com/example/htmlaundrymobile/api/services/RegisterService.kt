package com.example.htmlaundrymobile.api.services

import com.example.htmlaundrymobile.api.model.RegisterRequest
import com.example.htmlaundrymobile.api.model.RegisterResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RegisterService {
    @Multipart
    @POST("API/pelanggan/register")
    fun register(
        @Part("nama") nama: RequestBody,
        @Part("no_hp") no_hp: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody
    ): Call<RegisterResponse>
}
