package com.example.fashionsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionsapp.R
import com.example.fashionsapp.data.Size
import com.example.fashionsapp.data.sizeList
import com.example.fashionsapp.databinding.ItemSizeBinding

class AdapterSize(private val listener: OnItemSelectedListener) : RecyclerView.Adapter<AdapterSize.MyViewHolder>() {
    private var selectedItemPosition = RecyclerView.NO_POSITION
    private lateinit var binding: ItemSizeBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemSizeBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val size = sizeList[position]
        holder.bind(size)
        holder.itemView.isSelected = position == selectedItemPosition
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }

    inner class MyViewHolder(private val binding: ItemSizeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedItemPosition = position
                    listener.onItemSelected(position)
                    notifyDataSetChanged() // Cập nhật lại giao diện RecyclerView
                }
            }
        }

        fun bind(size: Size) {
            binding.sizeM.text = size.size
            val textColor = if (adapterPosition == selectedItemPosition) R.color.white else R.color.grey
            binding.sizeM.setTextColor(ContextCompat.getColor(itemView.context, textColor))
        }
    }
}
