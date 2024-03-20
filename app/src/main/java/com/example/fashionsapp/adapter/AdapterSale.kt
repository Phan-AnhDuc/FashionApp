package com.example.fashionsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionsapp.data.Sale
import com.example.fashionsapp.data.saleList
import com.example.fashionsapp.databinding.ItemSaleBinding

class AdapterSale : RecyclerView.Adapter<AdapterSale.MyViewHolder>()
{
    private lateinit var binding : ItemSaleBinding

    inner class MyViewHolder(private val binding: ItemSaleBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(sale: Sale) {
            binding.apply {
                saleText.text = "${sale.sale}% Off"
                codeText.text = "With code:${sale.code}"
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSaleBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return saleList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sale = saleList[position]
        holder.bind(sale)
    }
}