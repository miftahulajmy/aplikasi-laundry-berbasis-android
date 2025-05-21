package com.example.htmlaundrymobile.api.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("id_pelanggan") val idPelanggan: String,
    @SerializedName("tanggal_masuk") val tanggalMasuk: String,
    @SerializedName("tanggal_selesai") val tanggalSelesai: String
)

data class OrderResponse(
    @SerializedName("message") val message: String
)
