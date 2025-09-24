package com.example.powerplay.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.powerplay.R
import com.example.powerplay.data.model.Product
import com.example.powerplay.databinding.ItemProductBinding

class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                textViewTitle.text = product.title
                textViewDescription.text = product.description
                textViewCategory.text = product.category
                textViewPrice.text = "$${String.format("%.2f", product.price)}"

                // Load product image with Glide - use placeholder service for non-working URLs
                val workingImageUrl = getWorkingImageUrl(product)
                android.util.Log.d("ProductAdapter", "Loading image: $workingImageUrl")
                Glide.with(imageViewProduct.context)
                    .load(workingImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewProduct)

                // Set click listener
                root.setOnClickListener {
                    onProductClick(product)
                }
            }
        }

        private fun getWorkingImageUrl(product: Product): String {
            // Map of product keywords to working placeholder images
            val productImageMap = mapOf(
                "smartphone" to "https://picsum.photos/300/300?random=1",
                "earbuds" to "https://picsum.photos/300/300?random=2",
                "laptop" to "https://picsum.photos/300/300?random=3",
                "watch" to "https://picsum.photos/300/300?random=4",
                "mouse" to "https://picsum.photos/300/300?random=5",
                "charger" to "https://picsum.photos/300/300?random=6"
            )

            // Try to find a matching keyword in the product title
            val keyword = productImageMap.keys.find {
                product.title.lowercase().contains(it)
            }

            return productImageMap[keyword] ?: "https://picsum.photos/300/300?random=${product.id}"
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}