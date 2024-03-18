package com.example.masjidfirebaseapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.masjidfirebaseapp.R
import com.example.masjidfirebaseapp.databinding.ItemJamaahBinding
import com.example.masjidfirebaseapp.model.Jamaah

class JamaahAdapter(context: Context, private var listJamaah: ArrayList<Jamaah>?) :
    RecyclerView.Adapter<JamaahAdapter.JamaahViewHolder>() {

    private val listener: FireBaseDataListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JamaahViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jamaah, parent, false)
        return JamaahViewHolder(view)
    }

    override fun onBindViewHolder(holder: JamaahViewHolder, position: Int) {
        holder.bind(listJamaah?.get(position))
    }

    override fun getItemCount(): Int = listJamaah?.size!!

    inner class JamaahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemJamaahBinding.bind(itemView)
        fun bind(jamaah: Jamaah?) {
            binding.tvNamaJamaah.text = jamaah?.nama
            binding.tvAlamatJamaah.text = jamaah?.alamat
            binding.tvTglInput.text = jamaah?.tglInput
            binding.cvJamaah.setOnClickListener {
                listener.onDataClick(jamaah)
            }
        }
    }

    fun setSearchedList(listJamaah: ArrayList<Jamaah>) {
        this.listJamaah = listJamaah
        notifyDataSetChanged()
    }

    interface FireBaseDataListener {
        fun onDataClick(jamaah: Jamaah?)
    }

    init {
        listener = context as FireBaseDataListener
    }
}