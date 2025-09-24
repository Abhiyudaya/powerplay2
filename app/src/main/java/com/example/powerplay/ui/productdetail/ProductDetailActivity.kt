package com.example.powerplay.ui.productdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.powerplay.R
import com.example.powerplay.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PRODUCT_TITLE = "extra_product_title"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_CATEGORY = "extra_product_category"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayProductDetails()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = intent.getStringExtra(EXTRA_PRODUCT_TITLE) ?: "Product Details"
        }
    }

    private fun displayProductDetails() {
        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        val title = intent.getStringExtra(EXTRA_PRODUCT_TITLE) ?: ""
        val description = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION) ?: ""
        val category = intent.getStringExtra(EXTRA_PRODUCT_CATEGORY) ?: ""
        val price = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val imageUrl = intent.getStringExtra(EXTRA_PRODUCT_IMAGE)

        binding.apply {
            textViewDetailTitle.text = title
            textViewDetailDescription.text = description
            textViewDetailCategory.text = category
            textViewDetailPrice.text = "$${String.format("%.2f", price)}"

            // Load product image with Glide - use placeholder service for non-working URLs
            val workingImageUrl = getWorkingImageUrl(title, productId)
            Glide.with(this@ProductDetailActivity)
                .load(workingImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageViewProductDetail)
        }
    }

    private fun getWorkingImageUrl(title: String, productId: Int): String {
        // Map of product keywords to working placeholder images
        val productImageMap = mapOf(
            "smartphone" to "https://picsum.photos/400/400?random=1",
            "earbuds" to "https://picsum.photos/400/400?random=2",
            "laptop" to "https://picsum.photos/400/400?random=3",
            "watch" to "https://picsum.photos/400/400?random=4",
            "mouse" to "https://picsum.photos/400/400?random=5",
            "charger" to "https://picsum.photos/400/400?random=6"
        )

        // Try to find a matching keyword in the product title
        val keyword = productImageMap.keys.find {
            title.lowercase().contains(it)
        }

        return productImageMap[keyword] ?: "https://picsum.photos/400/400?random=$productId"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}