package com.example.htmlaundrymobile.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.htmlaundrymobile.R
import com.example.htmlaundrymobile.api.model.Transaksi

class TransaksiAdapter(
    private var transaksiList: List<Transaksi>,
    private val onClick: (Transaksi) -> Unit,
    private val onKeluhanSubmit: ((Int, String, EditText) -> Unit)? = null
) : RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    fun updateData(newTransaksiList: List<Transaksi>) {
        transaksiList = newTransaksiList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_transaksi, parent, false)
        return TransaksiViewHolder(view, onKeluhanSubmit == null)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        val transaksi = transaksiList[position]
        holder.bind(transaksi, onClick, onKeluhanSubmit)
    }

    override fun getItemCount(): Int {
        return transaksiList.size
    }

    class TransaksiViewHolder(itemView: View, private val hideKeluhanFields: Boolean) : RecyclerView.ViewHolder(itemView) {
        private val status: TextView = itemView.findViewById(R.id.status)
        private val statusBayar: TextView = itemView.findViewById(R.id.status_bayar)
        private val totalHarga: TextView = itemView.findViewById(R.id.total_harga)
        private val tanggalMasuk: TextView = itemView.findViewById(R.id.tanggal_masuk)
        private val tanggalSelesai: TextView = itemView.findViewById(R.id.tanggal_selesai)
        private val keluhanInput: EditText = itemView.findViewById(R.id.keluhan_input)
        private val keluhanSubmit: View = itemView.findViewById(R.id.keluhan_submit)
        private val keluhanText: TextView = itemView.findViewById(R.id.keluhan_display)

        fun bind(transaksi: Transaksi, onClick: (Transaksi) -> Unit, onKeluhanSubmit: ((Int, String, EditText) -> Unit)?) {
            status.text = transaksi.status
            statusBayar.text = "${transaksi.statusBayar} dibayar"
            totalHarga.text = transaksi.totalHarga.toString()
            tanggalMasuk.text = transaksi.tanggalMasuk
            tanggalSelesai.text = transaksi.tanggalSelesai

            if (hideKeluhanFields) {
                keluhanInput.visibility = View.GONE
                keluhanSubmit.visibility = View.GONE
                keluhanText.visibility = View.GONE
            } else {
                if (transaksi.keluhan.isNullOrEmpty()) {
                    keluhanInput.visibility = View.VISIBLE
                    keluhanSubmit.visibility = View.VISIBLE
                    keluhanText.visibility = View.GONE
                    keluhanSubmit.setOnClickListener {
                        val keluhan = keluhanInput.text.toString().trim()
                        if (keluhan.isNotEmpty()) {
                            onKeluhanSubmit?.invoke(transaksi.idTransaksi, keluhan, keluhanInput)
                        } else {
                            Toast.makeText(itemView.context, "Keluhan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    keluhanInput.visibility = View.GONE
                    keluhanSubmit.visibility = View.GONE
                    keluhanText.visibility = View.VISIBLE
                    keluhanText.text = transaksi.keluhan
                }
            }

            itemView.setOnClickListener { onClick(transaksi) }
        }
    }
}
