package com.example.fashionsapp

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionsapp.data.ItemFashion
import com.example.fashionsapp.data.fashionsList
import com.example.fashionsapp.databinding.ItemFashionsBinding
import com.example.fashionsapp.databinding.ItemSaleBinding

class AdapterFashion : RecyclerView.Adapter<AdapterFashion.MyViewHolder>() {


    inner class MyViewHolder(private val binding: ItemFashionsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fashion: ItemFashion) {
            binding.apply {
                titleFashion.text = fashion.title
                noteFashion.text = fashion.note
                moneyFashion.text = fashion.money
                Glide.with(itemView.context)
                    .load(fashion.urlImage)
                    .error(R.drawable.back_item)
                    .placeholder(R.drawable.back_item)
                    .into(image)
                itemView.setOnClickListener { onItemClickListener?.invoke(fashion)
                }
            }
        }
    }
        private var onItemClickListener: ((ItemFashion) -> Unit)? = null

        fun setOnItemClickListener(listener: (ItemFashion) -> Unit){
            onItemClickListener = listener
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFashionsBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fashionsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val fashions = fashionsList[position]
        holder.bind(fashions)
    }

}