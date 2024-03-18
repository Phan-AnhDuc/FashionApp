package com.example.fashionsapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.fashionsapp.data.ItemFashion
import com.example.fashionsapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        back()
        val fashion = intent.getParcelableExtra<ItemFashion>("fashion")
        if (fashion != null) {
            displayArticleDetails(fashion)
        } else {
            finish()
        }
    }

    private fun displayArticleDetails(fashion: ItemFashion) {
        binding?.apply {
          title.text = fashion.title
            note.text = fashion.note
            description.text = fashion.description
            money.text = fashion.money
            review.text = "(${fashion.review} Review)"
            Glide.with(this@DetailActivity)
                .load(fashion.urlImage)
                .error(R.drawable.image)
                .placeholder(R.drawable.image)
                .into(imageView5)
        }
    }

    private fun back(){
        binding?.buttonAdd?.setOnClickListener {
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}