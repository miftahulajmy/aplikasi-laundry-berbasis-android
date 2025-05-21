package com.example.htmlaundrymobile.api.model

data class JenisLayananResponse(
    val message: String,
    val data_jenislayanan: List<JenisLayanan>
)

data class JenisLayanan(
    val id: Int,
    val nama_layanan: String,
    val harga: Int,
    val waktu_pengerjaan: String,
    val deskripsi: String
)
