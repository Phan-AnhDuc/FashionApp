package com.example.fashionsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionsapp.data.Size
import com.example.fashionsapp.data.sizeList
import com.example.fashionsapp.databinding.ItemSizeBinding

class AdapterSize : RecyclerView.Adapter<AdapterSize.MyViewHolder>()
{
    private lateinit var binding : ItemSizeBinding
    class MyViewHolder(private val binding: ItemSizeBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(size: Size) {
            binding.apply {
                sizeM.text = size.size
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSizeBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val size = sizeList[position]
        holder.bind(size)
    }

}