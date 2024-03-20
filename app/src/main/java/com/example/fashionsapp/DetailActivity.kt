package com.example.fashionsapp

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.fashionsapp.adapter.AdapterSize
import com.example.fashionsapp.data.ItemFashion
import com.example.fashionsapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity(), AdapterSize.OnItemSelectedListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    private lateinit var sizeAdapter: AdapterSize

    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sizeAdapter = AdapterSize(this)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        back()
        val fashion = intent.getParcelableExtra<ItemFashion>("fashion")
        if (fashion != null) {
            displayArticleDetails(fashion)
            setImageSlider(fashion)
        } else {
            finish()
        }
        setupViewsHorizontal()
        setTransparentStatusBar()
        setQuantity()
    }

    private fun displayArticleDetails(fashion: ItemFashion) {
        val textReview = "(${fashion.review} Review)"
        binding?.apply {
            title.text = fashion.title
            note.text = fashion.note
            description.text = fashion.description
            money.text = fashion.money
            review.text = textReview
        }
    }

    private fun back() {
        binding?.buttonAdd?.setOnClickListener {
            finish()
        }
        binding?.buttonBack?.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewsHorizontal() {
        binding?.sizeRecyclerView?.apply {
            layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = sizeAdapter
        }
    }

    override fun onItemSelected(position: Int) {
        Log.d("ItemSelected", "$position")
    }

    private fun setImageSlider(fashion: ItemFashion) {
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(fashion.urlImage, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(fashion.urlImage1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(fashion.urlImage2, ScaleTypes.CENTER_CROP))

        binding?.imageSlider?.setImageList(imageList)
    }

    private fun Activity.setTransparentStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun setQuantity() {
        binding?.buttonPlus?.setOnClickListener {
            quantity++
            binding?.soLuong?.text = quantity.toString()
        }

        binding?.buttonMinus?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding?.soLuong?.text = quantity.toString()
            }
        }
    }
}