# Design Document

## Overview

This design implements a paginated product list application for Android using traditional Android Views (RecyclerView) with MVVM architecture. The app will fetch product data from the FakeAPI endpoint, display it in a scrollable list with automatic pagination, and provide detailed product views. The implementation prioritizes performance, error handling, and maintainable code structure.

## Architecture

### MVVM Pattern
- **Model**: Data classes representing API responses and business entities
- **View**: Activities and Fragments handling UI interactions
- **ViewModel**: Business logic and state management using Android Architecture Components

### Layer Structure
```
Presentation Layer (Activities/Fragments)
    ↓
ViewModel Layer (ViewModels + LiveData/StateFlow)
    ↓
Domain Layer (Use Cases/Interactors)
    ↓
Data Layer (Repository + Remote Data Source)
    ↓
Network Layer (Retrofit + API Service)
```

## Components and Interfaces

### 1. Data Models

#### Product Data Class
```kotlin
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val image: String?
)
```

#### API Response Models
```kotlin
data class ProductsResponse(
    val products: List<Product>,
    val nextPage: Int?,
    val totalPages: Int,
    val currentPage: Int
)
```

### 2. Network Layer

#### API Service Interface
```kotlin
interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("category") category: String = "electronics"
    ): Response<ProductsResponse>
}
```

#### Network Configuration
- **Base URL**: https://fakeapi.net/
- **Timeout**: 30 seconds for connection, 60 seconds for read
- **Retry Policy**: Exponential backoff for failed requests
- **Error Handling**: Custom exception mapping for different HTTP status codes

### 3. Repository Pattern

#### ProductRepository Interface
```kotlin
interface ProductRepository {
    suspend fun getProducts(page: Int): Result<ProductsResponse>
    suspend fun getProductById(id: Int): Result<Product>
}
```

#### Implementation Strategy
- Cache first page in memory for offline access
- Implement proper error mapping from network exceptions
- Handle pagination state management

### 4. UI Components

#### MainActivity
- Hosts the product list fragment
- Handles navigation to detail screen
- Manages toolbar and basic app structure

#### ProductListFragment
- Contains RecyclerView for product display
- Implements pull-to-refresh functionality
- Handles loading states and error messages
- Manages pagination scroll detection

#### ProductAdapter (RecyclerView.Adapter)
- Custom ViewHolder for product items
- Implements DiffUtil for efficient updates
- Handles image loading with Glide
- Manages click events for navigation

#### ProductDetailActivity
- Displays full product information
- Handles back navigation
- Implements shared element transitions (bonus)

### 5. ViewModels

#### ProductListViewModel
```kotlin
class ProductListViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadProducts(page: Int = 0)
    fun loadNextPage()
    fun retry()
}
```

## Data Models

### Product Entity
- **id**: Unique identifier (Int)
- **title**: Product name (String)
- **description**: Product description (String)
- **category**: Product category (String)
- **price**: Product price (Double)
- **image**: Product image URL (String?)

### UI State Models
```kotlin
sealed class UiState<T> {
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
}

data class PaginationState(
    val currentPage: Int = 0,
    val hasNextPage: Boolean = true,
    val isLoadingNextPage: Boolean = false
)
```

## Error Handling

### Network Error Types
1. **No Internet Connection**: Show dedicated offline screen with retry
2. **Server Errors (5xx)**: Show generic server error with retry
3. **Client Errors (4xx)**: Show appropriate user-friendly messages
4. **Timeout Errors**: Show timeout message with retry option
5. **Parsing Errors**: Show data format error message

### Error Recovery Strategy
- Implement exponential backoff for retries
- Cache last successful response for offline viewing
- Provide manual retry buttons in error states
- Graceful degradation when images fail to load

### Error UI Components
```kotlin
// Error states for different scenarios
sealed class ErrorState {
    object NoInternet : ErrorState()
    object ServerError : ErrorState()
    data class ApiError(val code: Int, val message: String) : ErrorState()
    object TimeoutError : ErrorState()
    object UnknownError : ErrorState()
}
```

## Testing Strategy

### Unit Tests
- **ViewModels**: Test business logic and state management
- **Repository**: Test data fetching and caching logic
- **Use Cases**: Test individual business operations
- **Utilities**: Test helper functions and extensions

### Integration Tests
- **API Service**: Test network calls with MockWebServer
- **Database Operations**: Test local data persistence
- **Repository Integration**: Test repository with real/mock dependencies

### UI Tests (Instrumented)
- **RecyclerView Interactions**: Test scrolling and item clicks
- **Navigation**: Test fragment/activity transitions
- **Error States**: Test error UI display and retry functionality
- **Pagination**: Test automatic loading of next pages

### Test Coverage Goals
- Minimum 80% code coverage for business logic
- 100% coverage for critical paths (data fetching, error handling)
- UI tests for all major user flows

## Performance Considerations

### Image Loading Optimization
- **Glide Configuration**: Memory and disk caching enabled
- **Image Sizing**: Resize images to match ImageView dimensions
- **Placeholder Strategy**: Show skeleton loaders during image load
- **Memory Management**: Clear image cache when memory is low

### RecyclerView Optimization
- **ViewHolder Pattern**: Efficient view recycling
- **DiffUtil**: Minimize unnecessary UI updates
- **Prefetch**: Load next page when user is 5 items from bottom
- **Item Animations**: Smooth animations for new items

### Network Optimization
- **Request Batching**: Load 10 items per page for optimal balance
- **Connection Pooling**: Reuse HTTP connections
- **Response Caching**: Cache responses for offline access
- **Background Threading**: All network calls on background threads

## Dependencies Required

### Core Dependencies
```kotlin
// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

// Image Loading
implementation 'com.github.bumptech.glide:glide:4.15.1'

// Architecture Components
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
implementation 'androidx.navigation:navigation-fragment-ktx:2.7.4'

// UI Components
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
implementation 'com.google.android.material:material:1.10.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

### Testing Dependencies
```kotlin
// Unit Testing
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:5.5.0'
testImplementation 'androidx.arch.core:core-testing:2.2.0'
testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'

// Integration Testing
androidTestImplementation 'com.squareup.okhttp3:mockwebserver:4.11.0'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
androidTestImplementation 'androidx.test.espresso:espresso-contrib:3.5.1'
```