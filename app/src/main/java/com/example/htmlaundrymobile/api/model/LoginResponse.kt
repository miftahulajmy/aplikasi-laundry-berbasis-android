package com.example.htmlaundrymobile.api.model

data class LoginResponse(
    val message: String,
    val id_pelanggan: Int,
    val nama: String,
    val token: String
)
