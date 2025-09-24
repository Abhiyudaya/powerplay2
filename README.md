# PowerPlay - Paginated Product List Android App

A modern Android application that displays a paginated list of products fetched from a REST API. Built using traditional Android Views with MVVM architecture.

## Features

- ✅ **Paginated Product List**: Displays products in a scrollable RecyclerView with automatic pagination
- ✅ **Product Details**: Tap any product to view detailed information
- ✅ **Pull-to-Refresh**: Swipe down to refresh the product list
- ✅ **Error Handling**: Comprehensive error handling with retry functionality
- ✅ **Loading States**: Visual feedback during data loading
- ✅ **Image Loading**: Efficient image loading with caching using Glide
- ✅ **Network Connectivity**: Detects and handles offline scenarios
- ✅ **Clean Architecture**: MVVM pattern with Repository and proper separation of concerns

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture with the following layers:

- **Presentation Layer**: Activities, Fragments, and Adapters
- **ViewModel Layer**: Business logic and state management
- **Repository Layer**: Data management and API abstraction
- **Network Layer**: Retrofit-based API communication

## Tech Stack

- **Language**: Kotlin
- **UI**: Traditional Android Views (RecyclerView, Fragments)
- **Architecture**: MVVM with LiveData
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **Dependency Injection**: Manual (ViewModelFactory)
- **Testing**: JUnit, Mockito, Espresso

## API

The app consumes the FakeAPI endpoint:
```
https://fakeapi.net/products?page=0&limit=10&category=electronics
```

## Project Structure

```
app/src/main/java/com/example/powerplay/
├── data/
│   ├── api/                 # API service interfaces
│   ├── model/              # Data models
│   ├── network/            # Network utilities and error handling
│   └── repository/         # Repository implementations
├── ui/
│   ├── adapter/            # RecyclerView adapters
│   ├── productdetail/      # Product detail screen
│   ├── productlist/        # Product list screen
│   ├── state/              # UI state management
│   └── viewmodel/          # ViewModelFactory
├── utils/                  # Utility classes
└── MainActivity.kt         # Main activity
```

## Key Components

### ProductListFragment
- Displays products in a RecyclerView
- Handles pagination through scroll detection
- Manages loading states and error handling
- Implements pull-to-refresh functionality

### ProductAdapter
- RecyclerView adapter with ViewHolder pattern
- Uses DiffUtil for efficient updates
- Handles image loading with Glide
- Manages click events for navigation

### ProductListViewModel
- Manages UI state and business logic
- Handles pagination state
- Coordinates with repository for data fetching
- Provides LiveData for UI observation

### ProductRepository
- Abstracts API calls from ViewModels
- Implements caching for better performance
- Handles network error mapping
- Provides clean data layer interface

## Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the app on device/emulator

```bash
./gradlew assembleDebug
```

## Testing

The project includes:
- Unit tests for ViewModels and Repository
- Instrumented tests for UI interactions
- Mock network responses for testing

```bash
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```

## Features Implemented

- [x] Paginated product list with RecyclerView
- [x] Product detail screen
- [x] Pull-to-refresh functionality
- [x] Error handling with retry
- [x] Loading states
- [x] Image loading with caching
- [x] Network connectivity detection
- [x] MVVM architecture
- [x] Unit and instrumented tests

## Future Enhancements

- Add search functionality
- Implement product filtering
- Add favorites/wishlist feature
- Implement offline caching with Room database
- Add shared element transitions
- Implement dependency injection with Hilt/Dagger

## License

This project is for educational purposes.