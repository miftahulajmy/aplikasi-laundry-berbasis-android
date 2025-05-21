package com.example.htmlaundrymobile.api.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nama") val nama: String,
    @SerializedName("no_hp") val no_hp: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

