package com.example.powerplay.ui.productlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.powerplay.data.model.Product
import com.example.powerplay.databinding.FragmentProductListBinding
import com.example.powerplay.ui.adapter.ProductAdapter
import com.example.powerplay.ui.productdetail.ProductDetailActivity
import com.example.powerplay.ui.state.UiState
import com.example.powerplay.ui.viewmodel.ViewModelFactory

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductListViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[ProductListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            navigateToProductDetail(product)
        }

        binding.recyclerViewProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
            
            // Add scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Load next page when user is near the bottom
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupClickListeners() {
        binding.buttonRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    showLoading(true)
                    showError(false)
                    showNoData(false)
                }
                is UiState.Success -> {
                    showLoading(false)
                    showError(false)
                    showNoData(state.data.isEmpty())
                }
                is UiState.Error -> {
                    showLoading(false)
                    showError(true, state.message)
                    showNoData(false)
                }
            }
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBarLoading.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerViewProducts.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(show: Boolean, message: String = "") {
        binding.layoutError.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            binding.textViewErrorMessage.text = message
        }
    }

    private fun showNoData(show: Boolean) {
        binding.layoutNoData.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun navigateToProductDetail(product: Product) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_TITLE, product.title)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESCRIPTION, product.description)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_CATEGORY, product.category)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.price)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_IMAGE, product.image)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}