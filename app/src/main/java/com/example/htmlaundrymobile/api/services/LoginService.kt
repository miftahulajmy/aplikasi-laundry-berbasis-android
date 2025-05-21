package com.example.htmlaundrymobile.api.services

import com.example.htmlaundrymobile.api.model.LoginRequest
import com.example.htmlaundrymobile.api.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("API/pelanggan/login/")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
