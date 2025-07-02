package com.anyoneide.app.templates

import com.anyoneide.app.core.ProjectTemplate
import java.io.File

/**
 * REST API Client template
 */
class RestApiClientTemplate : ProjectTemplate {
    
    override val id = "rest_api"
    override val name = "REST API Client"
    override val description = "Android app that consumes REST APIs with Retrofit and displays data in RecyclerView."
    override val category = "Android"
    override val features = listOf("Retrofit", "RecyclerView", "JSON Parsing", "Network Handling")
    override val difficulty = "Intermediate"
    override val estimatedTime = "50 min"
    
    override fun create(projectDir: File, packageName: String): Boolean {
        try {
            // Create project structure
            createBasicProjectStructure(projectDir, packageName)
            
            // Create app module
            val appDir = File(projectDir, "app")
            appDir.mkdirs()
            
            // Create build.gradle for app module
            val appBuildGradle = """
                plugins {
                    id 'com.android.application'
                    id 'org.jetbrains.kotlin.android'
                }
                
                android {
                    namespace '$packageName'
                    compileSdk 34
                    
                    defaultConfig {
                        applicationId "$packageName"
                        minSdk 24
                        targetSdk 34
                        versionCode 1
                        versionName "1.0"
                        
                        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
                    }
                    
                    buildTypes {
                        release {
                            minifyEnabled false
                            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
                        }
                    }
                    
                    compileOptions {
                        sourceCompatibility JavaVersion.VERSION_1_8
                        targetCompatibility JavaVersion.VERSION_1_8
                    }
                    
                    kotlinOptions {
                        jvmTarget = '1.8'
                    }
                    
                    buildFeatures {
                        viewBinding true
                    }
                }
                
                dependencies {
                    implementation 'androidx.core:core-ktx:1.12.0'
                    implementation 'androidx.appcompat:appcompat:1.6.1'
                    implementation 'com.google.android.material:material:1.11.0'
                    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
                    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
                    
                    // Retrofit for API calls
                    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
                    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
                    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
                    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
                    
                    // Coroutines
                    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
                    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
                    
                    // ViewModel and LiveData
                    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
                    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
                    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
                    implementation 'androidx.activity:activity-ktx:1.8.2'
                    
                    // Glide for image loading
                    implementation 'com.github.bumptech.glide:glide:4.16.0'
                    
                    // Testing
                    testImplementation 'junit:junit:4.13.2'
                    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
                    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
                }
            """.trimIndent()
            
            File(appDir, "build.gradle").writeText(appBuildGradle)
            
            // Create src directory structure
            val mainDir = File(appDir, "src/main")
            mainDir.mkdirs()
            
            // Create AndroidManifest.xml
            val manifest = """
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                    
                    <uses-permission android:name="android.permission.INTERNET" />
                    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                    
                    <application
                        android:name=".ApiApplication"
                        android:allowBackup="true"
                        android:icon="@mipmap/ic_launcher"
                        android:label="@string/app_name"
                        android:roundIcon="@mipmap/ic_launcher_round"
                        android:supportsRtl="true"
                        android:theme="@style/Theme.RestApiClient">
                        
                        <activity
                            android:name=".ui.MainActivity"
                            android:exported="true">
                            <intent-filter>
                                <action android:name="android.intent.action.MAIN" />
                                <category android:name="android.intent.category.LAUNCHER" />
                            </intent-filter>
                        </activity>
                        
                        <activity
                            android:name=".ui.DetailActivity"
                            android:exported="false" />
                        
                    </application>
                    
                </manifest>
            """.trimIndent()
            
            File(mainDir, "AndroidManifest.xml").writeText(manifest)
            
            // Create package structure
            val packagePath = packageName.replace(".", "/")
            val javaDir = File(mainDir, "java/$packagePath")
            
            // Create directories for API client structure
            val apiDir = File(javaDir, "api")
            val modelDir = File(javaDir, "model")
            val uiDir = File(javaDir, "ui")
            val utilDir = File(javaDir, "util")
            
            apiDir.mkdirs()
            modelDir.mkdirs()
            uiDir.mkdirs()
            utilDir.mkdirs()
            
            // Create Application class
            val applicationClass = """
                package $packageName
                
                import android.app.Application
                import $packageName.api.ApiService
                import $packageName.api.RetrofitClient
                
                class ApiApplication : Application() {
                    
                    // Using lazy so the API service is only created when needed
                    val apiService: ApiService by lazy {
                        RetrofitClient.create()
                    }
                    
                    override fun onCreate() {
                        super.onCreate()
                        // Initialize any application-wide components here
                    }
                }
            """.trimIndent()
            
            File(javaDir, "ApiApplication.kt").writeText(applicationClass)
            
            // Create RetrofitClient
            val retrofitClient = """
                package $packageName.api
                
                import okhttp3.OkHttpClient
                import okhttp3.logging.HttpLoggingInterceptor
                import retrofit2.Retrofit
                import retrofit2.converter.gson.GsonConverterFactory
                import java.util.concurrent.TimeUnit
                
                object RetrofitClient {
                    
                    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
                    
                    fun create(): ApiService {
                        val logger = HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                        
                        val client = OkHttpClient.Builder()
                            .addInterceptor(logger)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .build()
                        
                        return Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiService::class.java)
                    }
                }
            """.trimIndent()
            
            File(apiDir, "RetrofitClient.kt").writeText(retrofitClient)
            
            // Create ApiService
            val apiService = """
                package $packageName.api
                
                import $packageName.model.Post
                import $packageName.model.User
                import retrofit2.http.GET
                import retrofit2.http.Path
                
                interface ApiService {
                    
                    @GET("posts")
                    suspend fun getPosts(): List<Post>
                    
                    @GET("posts/{id}")
                    suspend fun getPost(@Path("id") id: Int): Post
                    
                    @GET("users")
                    suspend fun getUsers(): List<User>
                    
                    @GET("users/{id}")
                    suspend fun getUser(@Path("id") id: Int): User
                }
            """.trimIndent()
            
            File(apiDir, "ApiService.kt").writeText(apiService)
            
            // Create Post model
            val postModel = """
                package $packageName.model
                
                import com.google.gson.annotations.SerializedName
                
                data class Post(
                    @SerializedName("id") val id: Int,
                    @SerializedName("userId") val userId: Int,
                    @SerializedName("title") val title: String,
                    @SerializedName("body") val body: String
                )
            """.trimIndent()
            
            File(modelDir, "Post.kt").writeText(postModel)
            
            // Create User model
            val userModel = """
                package $packageName.model
                
                import com.google.gson.annotations.SerializedName
                
                data class User(
                    @SerializedName("id") val id: Int,
                    @SerializedName("name") val name: String,
                    @SerializedName("username") val username: String,
                    @SerializedName("email") val email: String,
                    @SerializedName("address") val address: Address,
                    @SerializedName("phone") val phone: String,
                    @SerializedName("website") val website: String,
                    @SerializedName("company") val company: Company
                )
                
                data class Address(
                    @SerializedName("street") val street: String,
                    @SerializedName("suite") val suite: String,
                    @SerializedName("city") val city: String,
                    @SerializedName("zipcode") val zipcode: String,
                    @SerializedName("geo") val geo: Geo
                )
                
                data class Geo(
                    @SerializedName("lat") val lat: String,
                    @SerializedName("lng") val lng: String
                )
                
                data class Company(
                    @SerializedName("name") val name: String,
                    @SerializedName("catchPhrase") val catchPhrase: String,
                    @SerializedName("bs") val bs: String
                )
            """.trimIndent()
            
            File(modelDir, "User.kt").writeText(userModel)
            
            // Create MainActivity
            val mainActivity = """
                package $packageName.ui
                
                import android.os.Bundle
                import android.view.View
                import android.widget.Toast
                import androidx.activity.viewModels
                import androidx.appcompat.app.AppCompatActivity
                import androidx.lifecycle.lifecycleScope
                import androidx.recyclerview.widget.LinearLayoutManager
                import $packageName.ApiApplication
                import $packageName.databinding.ActivityMainBinding
                import kotlinx.coroutines.launch
                
                class MainActivity : AppCompatActivity() {
                    
                    private lateinit var binding: ActivityMainBinding
                    private lateinit var adapter: PostAdapter
                    
                    private val viewModel: MainViewModel by viewModels {
                        MainViewModelFactory((application as ApiApplication).apiService)
                    }
                    
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        binding = ActivityMainBinding.inflate(layoutInflater)
                        setContentView(binding.root)
                        
                        setupRecyclerView()
                        observeViewModel()
                        setupSwipeRefresh()
                        
                        // Initial load
                        viewModel.loadPosts()
                    }
                    
                    private fun setupRecyclerView() {
                        adapter = PostAdapter { post ->
                            // Navigate to detail screen
                            val intent = DetailActivity.createIntent(this, post.id)
                            startActivity(intent)
                        }
                        
                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    }
                    
                    private fun observeViewModel() {
                        // Observe posts
                        viewModel.posts.observe(this) { result ->
                            when (result) {
                                is ApiResult.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.errorView.visibility = View.GONE
                                }
                                is ApiResult.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.errorView.visibility = View.GONE
                                    binding.swipeRefresh.isRefreshing = false
                                    
                                    adapter.submitList(result.data)
                                    
                                    // Show empty view if list is empty
                                    if (result.data.isEmpty()) {
                                        binding.emptyView.visibility = View.VISIBLE
                                    } else {
                                        binding.emptyView.visibility = View.GONE
                                    }
                                }
                                is ApiResult.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.swipeRefresh.isRefreshing = false
                                    
                                    // Show error view
                                    binding.errorView.visibility = View.VISIBLE
                                    binding.errorText.text = result.message
                                    
                                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    
                    private fun setupSwipeRefresh() {
                        binding.swipeRefresh.setOnRefreshListener {
                            viewModel.loadPosts()
                        }
                    }
                }
            """.trimIndent()
            
            File(uiDir, "MainActivity.kt").writeText(mainActivity)
            
            // Create MainViewModel
            val mainViewModel = """
                package $packageName.ui
                
                import androidx.lifecycle.LiveData
                import androidx.lifecycle.MutableLiveData
                import androidx.lifecycle.ViewModel
                import androidx.lifecycle.ViewModelProvider
                import androidx.lifecycle.viewModelScope
                import $packageName.api.ApiService
                import $packageName.model.Post
                import kotlinx.coroutines.launch
                import java.io.IOException
                
                class MainViewModel(private val apiService: ApiService) : ViewModel() {
                    
                    private val _posts = MutableLiveData<ApiResult<List<Post>>>()
                    val posts: LiveData<ApiResult<List<Post>>> = _posts
                    
                    fun loadPosts() {
                        viewModelScope.launch {
                            _posts.value = ApiResult.Loading()
                            
                            try {
                                val response = apiService.getPosts()
                                _posts.value = ApiResult.Success(response)
                            } catch (e: IOException) {
                                _posts.value = ApiResult.Error("Network error: ${e.message}")
                            } catch (e: Exception) {
                                _posts.value = ApiResult.Error("Error: ${e.message}")
                            }
                        }
                    }
                }
                
                class MainViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return MainViewModel(apiService) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
                
                sealed class ApiResult<T> {
                    class Loading<T> : ApiResult<T>()
                    data class Success<T>(val data: T) : ApiResult<T>()
                    data class Error<T>(val message: String) : ApiResult<T>()
                }
            """.trimIndent()
            
            File(uiDir, "MainViewModel.kt").writeText(mainViewModel)
            
            // Create PostAdapter
            val postAdapter = """
                package $packageName.ui
                
                import android.view.LayoutInflater
                import android.view.ViewGroup
                import androidx.recyclerview.widget.DiffUtil
                import androidx.recyclerview.widget.ListAdapter
                import androidx.recyclerview.widget.RecyclerView
                import $packageName.databinding.ItemPostBinding
                import $packageName.model.Post
                
                class PostAdapter(private val onPostClicked: (Post) -> Unit) : 
                    ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback) {
                    
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                        val binding = ItemPostBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        return PostViewHolder(binding)
                    }
                    
                    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
                        val post = getItem(position)
                        holder.bind(post)
                    }
                    
                    inner class PostViewHolder(private val binding: ItemPostBinding) : 
                        RecyclerView.ViewHolder(binding.root) {
                        
                        init {
                            binding.root.setOnClickListener {
                                val position = bindingAdapterPosition
                                if (position != RecyclerView.NO_POSITION) {
                                    onPostClicked(getItem(position))
                                }
                            }
                        }
                        
                        fun bind(post: Post) {
                            binding.textTitle.text = post.title
                            binding.textBody.text = post.body
                        }
                    }
                    
                    object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
                        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                            return oldItem.id == newItem.id
                        }
                        
                        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                            return oldItem == newItem
                        }
                    }
                }
            """.trimIndent()
            
            File(uiDir, "PostAdapter.kt").writeText(postAdapter)
            
            // Create DetailActivity
            val detailActivity = """
                package $packageName.ui
                
                import android.content.Context
                import android.content.Intent
                import android.os.Bundle
                import android.view.View
                import android.widget.Toast
                import androidx.activity.viewModels
                import androidx.appcompat.app.AppCompatActivity
                import $packageName.ApiApplication
                import $packageName.databinding.ActivityDetailBinding
                
                class DetailActivity : AppCompatActivity() {
                    
                    private lateinit var binding: ActivityDetailBinding
                    
                    private val viewModel: DetailViewModel by viewModels {
                        DetailViewModelFactory((application as ApiApplication).apiService)
                    }
                    
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        binding = ActivityDetailBinding.inflate(layoutInflater)
                        setContentView(binding.root)
                        
                        // Set up back button
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        
                        // Get post ID from intent
                        val postId = intent.getIntExtra(EXTRA_POST_ID, -1)
                        if (postId == -1) {
                            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show()
                            finish()
                            return
                        }
                        
                        // Load post details
                        viewModel.loadPostDetails(postId)
                        
                        // Observe post details
                        viewModel.post.observe(this) { result ->
                            when (result) {
                                is ApiResult.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.contentLayout.visibility = View.GONE
                                    binding.errorView.visibility = View.GONE
                                }
                                is ApiResult.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.contentLayout.visibility = View.VISIBLE
                                    binding.errorView.visibility = View.GONE
                                    
                                    val post = result.data
                                    binding.textTitle.text = post.title
                                    binding.textBody.text = post.body
                                    
                                    // Load user details
                                    viewModel.loadUserDetails(post.userId)
                                }
                                is ApiResult.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.contentLayout.visibility = View.GONE
                                    binding.errorView.visibility = View.VISIBLE
                                    binding.errorText.text = result.message
                                    
                                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        
                        // Observe user details
                        viewModel.user.observe(this) { result ->
                            when (result) {
                                is ApiResult.Loading -> {
                                    binding.userProgressBar.visibility = View.VISIBLE
                                    binding.userInfo.visibility = View.GONE
                                }
                                is ApiResult.Success -> {
                                    binding.userProgressBar.visibility = View.GONE
                                    binding.userInfo.visibility = View.VISIBLE
                                    
                                    val user = result.data
                                    binding.textAuthor.text = "Author: ${user.name}"
                                    binding.textEmail.text = "Email: ${user.email}"
                                    binding.textWebsite.text = "Website: ${user.website}"
                                }
                                is ApiResult.Error -> {
                                    binding.userProgressBar.visibility = View.GONE
                                    binding.userInfo.visibility = View.GONE
                                    
                                    Toast.makeText(this, "Failed to load user: ${result.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    
                    override fun onSupportNavigateUp(): Boolean {
                        onBackPressed()
                        return true
                    }
                    
                    companion object {
                        private const val EXTRA_POST_ID = "extra_post_id"
                        
                        fun createIntent(context: Context, postId: Int): Intent {
                            return Intent(context, DetailActivity::class.java).apply {
                                putExtra(EXTRA_POST_ID, postId)
                            }
                        }
                    }
                }
            """.trimIndent()
            
            File(uiDir, "DetailActivity.kt").writeText(detailActivity)
            
            // Create DetailViewModel
            val detailViewModel = """
                package $packageName.ui
                
                import androidx.lifecycle.LiveData
                import androidx.lifecycle.MutableLiveData
                import androidx.lifecycle.ViewModel
                import androidx.lifecycle.ViewModelProvider
                import androidx.lifecycle.viewModelScope
                import $packageName.api.ApiService
                import $packageName.model.Post
                import $packageName.model.User
                import kotlinx.coroutines.launch
                import java.io.IOException
                
                class DetailViewModel(private val apiService: ApiService) : ViewModel() {
                    
                    private val _post = MutableLiveData<ApiResult<Post>>()
                    val post: LiveData<ApiResult<Post>> = _post
                    
                    private val _user = MutableLiveData<ApiResult<User>>()
                    val user: LiveData<ApiResult<User>> = _user
                    
                    fun loadPostDetails(postId: Int) {
                        viewModelScope.launch {
                            _post.value = ApiResult.Loading()
                            
                            try {
                                val response = apiService.getPost(postId)
                                _post.value = ApiResult.Success(response)
                            } catch (e: IOException) {
                                _post.value = ApiResult.Error("Network error: ${e.message}")
                            } catch (e: Exception) {
                                _post.value = ApiResult.Error("Error: ${e.message}")
                            }
                        }
                    }
                    
                    fun loadUserDetails(userId: Int) {
                        viewModelScope.launch {
                            _user.value = ApiResult.Loading()
                            
                            try {
                                val response = apiService.getUser(userId)
                                _user.value = ApiResult.Success(response)
                            } catch (e: IOException) {
                                _user.value = ApiResult.Error("Network error: ${e.message}")
                            } catch (e: Exception) {
                                _user.value = ApiResult.Error("Error: ${e.message}")
                            }
                        }
                    }
                }
                
                class DetailViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return DetailViewModel(apiService) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            """.trimIndent()
            
            File(uiDir, "DetailViewModel.kt").writeText(detailViewModel)
            
            // Create NetworkUtils
            val networkUtils = """
                package $packageName.util
                
                import android.content.Context
                import android.net.ConnectivityManager
                import android.net.NetworkCapabilities
                import android.os.Build
                
                object NetworkUtils {
                    
                    fun isNetworkAvailable(context: Context): Boolean {
                        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val network = connectivityManager.activeNetwork ?: return false
                            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                            
                            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                   capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        } else {
                            @Suppress("DEPRECATION")
                            val networkInfo = connectivityManager.activeNetworkInfo
                            @Suppress("DEPRECATION")
                            return networkInfo != null && networkInfo.isConnected
                        }
                    }
                }
            """.trimIndent()
            
            File(utilDir, "NetworkUtils.kt").writeText(networkUtils)
            
            // Create res directory structure
            val resDir = File(mainDir, "res")
            val layoutDir = File(resDir, "layout")
            val valuesDir = File(resDir, "values")
            layoutDir.mkdirs()
            valuesDir.mkdirs()
            
            // Create activity_main.xml
            val activityMain = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:context=".ui.MainActivity">
                    
                    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
                        android:id="@+id/swipeRefresh"
                        android:layout_width="0dp"
                        android:layout_height="0dp"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent">
                        
                        <androidx.recyclerview.widget.RecyclerView
                            android:id="@+id/recyclerView"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:clipToPadding="false"
                            android:padding="8dp"
                            tools:listitem="@layout/item_post" />
                            
                    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
                    
                    <ProgressBar
                        android:id="@+id/progressBar"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:visibility="gone"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />
                    
                    <TextView
                        android:id="@+id/emptyView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="No posts available"
                        android:textSize="16sp"
                        android:visibility="gone"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />
                    
                    <LinearLayout
                        android:id="@+id/errorView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:visibility="gone"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent">
                        
                        <TextView
                            android:id="@+id/errorText"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="Error loading data"
                            android:textSize="16sp" />
                        
                    </LinearLayout>
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "activity_main.xml").writeText(activityMain)
            
            // Create item_post.xml
            val itemPost = """
                <?xml version="1.0" encoding="utf-8"?>
                <com.google.android.material.card.MaterialCardView 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_margin="8dp"
                    app:cardElevation="4dp"
                    app:cardCornerRadius="8dp">
                    
                    <androidx.constraintlayout.widget.ConstraintLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:padding="16dp">
                        
                        <TextView
                            android:id="@+id/textTitle"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:textSize="18sp"
                            android:textStyle="bold"
                            android:maxLines="2"
                            android:ellipsize="end"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent"
                            tools:text="Post Title" />
                        
                        <TextView
                            android:id="@+id/textBody"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="8dp"
                            android:textSize="14sp"
                            android:maxLines="3"
                            android:ellipsize="end"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toBottomOf="@+id/textTitle"
                            tools:text="Post body text goes here..." />
                        
                    </androidx.constraintlayout.widget.ConstraintLayout>
                    
                </com.google.android.material.card.MaterialCardView>
            """.trimIndent()
            
            File(layoutDir, "item_post.xml").writeText(itemPost)
            
            // Create activity_detail.xml
            val activityDetail = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:context=".ui.DetailActivity">
                    
                    <androidx.core.widget.NestedScrollView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:fillViewport="true">
                        
                        <androidx.constraintlayout.widget.ConstraintLayout
                            android:id="@+id/contentLayout"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:padding="16dp">
                            
                            <TextView
                                android:id="@+id/textTitle"
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:textSize="24sp"
                                android:textStyle="bold"
                                app:layout_constraintEnd_toEndOf="parent"
                                app:layout_constraintStart_toStartOf="parent"
                                app:layout_constraintTop_toTopOf="parent"
                                tools:text="Post Title" />
                            
                            <TextView
                                android:id="@+id/textBody"
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_marginTop="16dp"
                                android:textSize="16sp"
                                app:layout_constraintEnd_toEndOf="parent"
                                app:layout_constraintStart_toStartOf="parent"
                                app:layout_constraintTop_toBottomOf="@+id/textTitle"
                                tools:text="Post body text goes here..." />
                            
                            <com.google.android.material.card.MaterialCardView
                                android:id="@+id/userInfo"
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_marginTop="24dp"
                                app:cardCornerRadius="8dp"
                                app:cardElevation="4dp"
                                app:layout_constraintEnd_toEndOf="parent"
                                app:layout_constraintStart_toStartOf="parent"
                                app:layout_constraintTop_toBottomOf="@+id/textBody">
                                
                                <LinearLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:orientation="vertical"
                                    android:padding="16dp">
                                    
                                    <TextView
                                        android:layout_width="wrap_content"
                                        android:layout_height="wrap_content"
                                        android:text="Author Information"
                                        android:textSize="18sp"
                                        android:textStyle="bold" />
                                    
                                    <TextView
                                        android:id="@+id/textAuthor"
                                        android:layout_width="wrap_content"
                                        android:layout_height="wrap_content"
                                        android:layout_marginTop="8dp"
                                        tools:text="Author: John Doe" />
                                    
                                    <TextView
                                        android:id="@+id/textEmail"
                                        android:layout_width="wrap_content"
                                        android:layout_height="wrap_content"
                                        android:layout_marginTop="4dp"
                                        tools:text="Email: john@example.com" />
                                    
                                    <TextView
                                        android:id="@+id/textWebsite"
                                        android:layout_width="wrap_content"
                                        android:layout_height="wrap_content"
                                        android:layout_marginTop="4dp"
                                        tools:text="Website: example.com" />
                                    
                                </LinearLayout>
                                
                            </com.google.android.material.card.MaterialCardView>
                            
                            <ProgressBar
                                android:id="@+id/userProgressBar"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_marginTop="24dp"
                                android:visibility="gone"
                                app:layout_constraintEnd_toEndOf="parent"
                                app:layout_constraintStart_toStartOf="parent"
                                app:layout_constraintTop_toBottomOf="@+id/textBody" />
                            
                        </androidx.constraintlayout.widget.ConstraintLayout>
                        
                    </androidx.core.widget.NestedScrollView>
                    
                    <ProgressBar
                        android:id="@+id/progressBar"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:visibility="gone"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />
                    
                    <LinearLayout
                        android:id="@+id/errorView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:visibility="gone"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent">
                        
                        <TextView
                            android:id="@+id/errorText"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="Error loading data"
                            android:textSize="16sp" />
                        
                    </LinearLayout>
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "activity_detail.xml").writeText(activityDetail)
            
            // Create strings.xml
            val strings = """
                <resources>
                    <string name="app_name">REST API Client</string>
                    <string name="action_settings">Settings</string>
                    <string name="action_refresh">Refresh</string>
                </resources>
            """.trimIndent()
            
            File(valuesDir, "strings.xml").writeText(strings)
            
            // Create colors.xml
            val colors = """
                <resources>
                    <color name="purple_200">#FFBB86FC</color>
                    <color name="purple_500">#FF6200EE</color>
                    <color name="purple_700">#FF3700B3</color>
                    <color name="teal_200">#FF03DAC5</color>
                    <color name="teal_700">#FF018786</color>
                    <color name="black">#FF000000</color>
                    <color name="white">#FFFFFFFF</color>
                </resources>
            """.trimIndent()
            
            File(valuesDir, "colors.xml").writeText(colors)
            
            // Create themes.xml
            val themes = """
                <resources>
                    <!-- Base application theme. -->
                    <style name="Theme.RestApiClient" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
                        <!-- Primary brand color. -->
                        <item name="colorPrimary">@color/purple_500</item>
                        <item name="colorPrimaryVariant">@color/purple_700</item>
                        <item name="colorOnPrimary">@color/white</item>
                        <!-- Secondary brand color. -->
                        <item name="colorSecondary">@color/teal_200</item>
                        <item name="colorSecondaryVariant">@color/teal_700</item>
                        <item name="colorOnSecondary">@color/black</item>
                        <!-- Status bar color. -->
                        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
                    </style>
                </resources>
            """.trimIndent()
            
            File(valuesDir, "themes.xml").writeText(themes)
            
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    private fun createBasicProjectStructure(projectDir: File, packageName: String) {
        // Create root build.gradle
        val rootBuildGradle = """
            // Top-level build file where you can add configuration options common to all sub-projects/modules.
            plugins {
                id 'com.android.application' version '8.1.4' apply false
                id 'com.android.library' version '8.1.4' apply false
                id 'org.jetbrains.kotlin.android' version '1.9.20' apply false
            }
            
            task clean(type: Delete) {
                delete rootProject.buildDir
            }
        """.trimIndent()
        
        File(projectDir, "build.gradle").writeText(rootBuildGradle)
        
        // Create settings.gradle
        val settingsGradle = """
            rootProject.name = "${projectDir.name}"
            include ':app'
        """.trimIndent()
        
        File(projectDir, "settings.gradle").writeText(settingsGradle)
        
        // Create gradle.properties
        val gradleProperties = """
            # Project-wide Gradle settings.
            org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
            android.useAndroidX=true
            kotlin.code.style=official
            android.nonTransitiveRClass=true
        """.trimIndent()
        
        File(projectDir, "gradle.properties").writeText(gradleProperties)
        
        // Create gradle wrapper directory
        val gradleWrapperDir = File(projectDir, "gradle/wrapper")
        gradleWrapperDir.mkdirs()
        
        // Create .gitignore
        val gitignore = """
            *.iml
            .gradle
            /local.properties
            /.idea
            .DS_Store
            /build
            /captures
            .externalNativeBuild
            .cxx
            local.properties
        """.trimIndent()
        
        File(projectDir, ".gitignore").writeText(gitignore)
    }
}