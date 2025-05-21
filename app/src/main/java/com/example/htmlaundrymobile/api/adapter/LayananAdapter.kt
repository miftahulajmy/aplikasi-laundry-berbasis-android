package com.example.htmlaundrymobile.api.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htmlaundrymobile.R
import com.example.htmlaundrymobile.api.model.JenisLayanan

class LayananAdapter(private val jenisLayananList: List<JenisLayanan>) :
    RecyclerView.Adapter<LayananAdapter.LayananViewHolder>() {

    class LayananViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layananName: TextView = view.findViewById(R.id.namaLayanan)
        val layananHarga: TextView = view.findViewById(R.id.hargaTextView)
        val layananWaktu: TextView = view.findViewById(R.id.waktuTextView)
        val layananDeskripsi: TextView = view.findViewById(R.id.deskripsiTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layanan, parent, false)
        return LayananViewHolder(view)
    }

    override fun onBindViewHolder(holder: LayananViewHolder, position: Int) {
        val layanan = jenisLayananList[position]
        holder.layananName.text = layanan.nama_layanan
        holder.layananHarga.text = "Harga: Rp.${layanan.harga} "
        holder.layananWaktu.text = "Waktu pengerjaan: ${layanan.waktu_pengerjaan} Hari"
        holder.layananDeskripsi.text = layanan.deskripsi

        // Logging for debugging
        Log.d("LayananAdapter", "Nama Layanan: ${layanan.nama_layanan}")
        Log.d("LayananAdapter", "Harga: ${layanan.harga}")
        Log.d("LayananAdapter", "Waktu pengerjaan: ${layanan.waktu_pengerjaan}")
        Log.d("LayananAdapter", "Deskripsi: ${layanan.deskripsi}")
    }

    override fun getItemCount(): Int {
        return jenisLayananList.size
    }
}
