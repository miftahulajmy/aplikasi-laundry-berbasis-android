package com.example.htmlaundrymobile.api.services


import com.example.htmlaundrymobile.api.model.JenisLayananResponse
import retrofit2.Call
import retrofit2.http.GET

interface LayananService {
    @GET("API/jenislayanan/")
    fun getJenisLayanan(): Call<JenisLayananResponse>
}
