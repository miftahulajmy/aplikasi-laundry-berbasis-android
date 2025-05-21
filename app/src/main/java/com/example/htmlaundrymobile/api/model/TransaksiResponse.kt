package com.example.htmlaundrymobile.api.model

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

data class Transaksi(
    @SerializedName("id_transaksi")
    val idTransaksi: Int,
    @SerializedName("id_pegawai")
    val idPegawai: Int,
    @SerializedName("id_pelanggan")
    val idPelanggan: Int,
    @SerializedName("total_harga")
    val totalHarga: String,
    @SerializedName("tanggal_masuk")
    val tanggalMasuk: String,
    @SerializedName("tanggal_selesai")
    val tanggalSelesai: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_bayar")
    val statusBayar: String,
    @SerializedName("keluhan")
    val keluhan: String?,
    @SerializedName("created_at")
    val createdAt: String
)


