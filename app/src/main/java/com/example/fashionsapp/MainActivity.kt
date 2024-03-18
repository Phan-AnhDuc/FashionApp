package com.example.fashionsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionsapp.data.ItemFashion
import com.example.fashionsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private var _binding : ActivityMainBinding?=null
    private val binding get() = _binding

    private lateinit var saleAdapter : AdapterSale
    private lateinit var fashionAdapter : AdapterFashion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        saleAdapter = AdapterSale()
        fashionAdapter = AdapterFashion()
        setContentView(binding?.root)
        setupViewsHorizontal()
        setupViewsVertical()
        fashionAdapter.setOnItemClickListener { fashion ->
            goToDetail(fashion)
        }

    }

    private fun goToDetail(fashion: ItemFashion) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("fashion",fashion)
        startActivity(intent)
    }


    private fun setupViewsHorizontal()
    {
        binding?.recyclerviewHorizontal?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity,
                LinearLayoutManager.HORIZONTAL,false)
            adapter = saleAdapter
        }
    }

    private fun setupViewsVertical()
    {
        binding?.recyclerviewVertical?.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = fashionAdapter
        }
    }
    override fun onDestroy()
    {
        super.onDestroy()
        _binding = null
    }


}