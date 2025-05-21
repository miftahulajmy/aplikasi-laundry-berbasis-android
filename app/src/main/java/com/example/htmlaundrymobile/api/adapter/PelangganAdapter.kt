package com.example.htmlaundrymobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htmlaundrymobile.api.model.Pelanggan

class PelangganAdapter(private val pelangganList: List<Pelanggan>) :
    RecyclerView.Adapter<PelangganAdapter.PelangganViewHolder>() {

    class PelangganViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaTextView: TextView = itemView.findViewById(R.id.nama)
        val noHpTextView: TextView = itemView.findViewById(R.id.no_hp)
        val alamatTextView: TextView = itemView.findViewById(R.id.alamat)
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PelangganViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pelanggan, parent, false)
        return PelangganViewHolder(view)
    }

    override fun onBindViewHolder(holder: PelangganViewHolder, position: Int) {
        val pelanggan = pelangganList[position]
        holder.namaTextView.text = pelanggan.nama
        holder.noHpTextView.text = pelanggan.no_hp
        holder.alamatTextView.text = pelanggan.alamat
        holder.usernameTextView.text = pelanggan.username
    }

    override fun getItemCount() = pelangganList.size
}
