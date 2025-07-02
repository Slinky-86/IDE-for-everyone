package com.anyoneide.app.templates

import com.anyoneide.app.core.ProjectTemplate
import java.io.File

/**
 * MVVM Architecture Application template
 */
class MvvmAppTemplate : ProjectTemplate {
    
    override val id = "mvvm_app"
    override val name = "MVVM Architecture"
    override val description = "Android app following MVVM pattern with Repository, ViewModel, and LiveData."
    override val category = "Android"
    override val features = listOf("MVVM Pattern", "Repository Pattern", "LiveData", "Room Database")
    override val difficulty = "Advanced"
    override val estimatedTime = "60 min"
    
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
                    id 'kotlin-kapt'
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
                    
                    // ViewModel and LiveData
                    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
                    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
                    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
                    implementation 'androidx.activity:activity-ktx:1.8.2'
                    implementation 'androidx.fragment:fragment-ktx:1.6.2'
                    
                    // Room components
                    implementation 'androidx.room:room-runtime:2.6.1'
                    implementation 'androidx.room:room-ktx:2.6.1'
                    kapt 'androidx.room:room-compiler:2.6.1'
                    
                    // Coroutines
                    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
                    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
                    
                    // Navigation
                    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.5'
                    implementation 'androidx.navigation:navigation-ui-ktx:2.7.5'
                    
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
                    
                    <application
                        android:name=".MyApplication"
                        android:allowBackup="true"
                        android:icon="@mipmap/ic_launcher"
                        android:label="@string/app_name"
                        android:roundIcon="@mipmap/ic_launcher_round"
                        android:supportsRtl="true"
                        android:theme="@style/Theme.MvvmApp">
                        
                        <activity
                            android:name=".ui.MainActivity"
                            android:exported="true">
                            <intent-filter>
                                <action android:name="android.intent.action.MAIN" />
                                <category android:name="android.intent.category.LAUNCHER" />
                            </intent-filter>
                        </activity>
                        
                    </application>
                    
                </manifest>
            """.trimIndent()
            
            File(mainDir, "AndroidManifest.xml").writeText(manifest)
            
            // Create package structure
            val packagePath = packageName.replace(".", "/")
            val javaDir = File(mainDir, "java/$packagePath")
            
            // Create directories for MVVM structure
            val dataDir = File(javaDir, "data")
            val repositoryDir = File(dataDir, "repository")
            val localDir = File(dataDir, "local")
            val remoteDir = File(dataDir, "remote")
            val modelDir = File(dataDir, "model")
            
            val uiDir = File(javaDir, "ui")
            val viewmodelDir = File(javaDir, "viewmodel")
            val utilDir = File(javaDir, "util")
            
            repositoryDir.mkdirs()
            localDir.mkdirs()
            remoteDir.mkdirs()
            modelDir.mkdirs()
            uiDir.mkdirs()
            viewmodelDir.mkdirs()
            utilDir.mkdirs()
            
            // Create Application class
            val applicationClass = """
                package $packageName
                
                import android.app.Application
                import $packageName.data.local.AppDatabase
                import $packageName.data.repository.UserRepository
                
                class MyApplication : Application() {
                    
                    // Using lazy so the database and repository are only created when needed
                    val database by lazy { AppDatabase.getDatabase(this) }
                    val repository by lazy { UserRepository(database.userDao()) }
                    
                    override fun onCreate() {
                        super.onCreate()
                        // Initialize any application-wide components here
                    }
                }
            """.trimIndent()
            
            File(javaDir, "MyApplication.kt").writeText(applicationClass)
            
            // Create User model
            val userModel = """
                package $packageName.data.model
                
                import androidx.room.Entity
                import androidx.room.PrimaryKey
                
                @Entity(tableName = "users")
                data class User(
                    @PrimaryKey(autoGenerate = true) val id: Int = 0,
                    val name: String,
                    val email: String,
                    val age: Int
                )
            """.trimIndent()
            
            File(modelDir, "User.kt").writeText(userModel)
            
            // Create Room Database
            val appDatabase = """
                package $packageName.data.local
                
                import android.content.Context
                import androidx.room.Database
                import androidx.room.Room
                import androidx.room.RoomDatabase
                import $packageName.data.model.User
                
                @Database(entities = [User::class], version = 1, exportSchema = false)
                abstract class AppDatabase : RoomDatabase() {
                    
                    abstract fun userDao(): UserDao
                    
                    companion object {
                        @Volatile
                        private var INSTANCE: AppDatabase? = null
                        
                        fun getDatabase(context: Context): AppDatabase {
                            return INSTANCE ?: synchronized(this) {
                                val instance = Room.databaseBuilder(
                                    context.applicationContext,
                                    AppDatabase::class.java,
                                    "app_database"
                                )
                                .fallbackToDestructiveMigration()
                                .build()
                                INSTANCE = instance
                                instance
                            }
                        }
                    }
                }
            """.trimIndent()
            
            File(localDir, "AppDatabase.kt").writeText(appDatabase)
            
            // Create UserDao
            val userDao = """
                package $packageName.data.local
                
                import androidx.lifecycle.LiveData
                import androidx.room.*
                import $packageName.data.model.User
                
                @Dao
                interface UserDao {
                    
                    @Query("SELECT * FROM users ORDER BY name ASC")
                    fun getAllUsers(): LiveData<List<User>>
                    
                    @Query("SELECT * FROM users WHERE id = :id")
                    fun getUserById(id: Int): LiveData<User>
                    
                    @Insert(onConflict = OnConflictStrategy.REPLACE)
                    suspend fun insert(user: User)
                    
                    @Update
                    suspend fun update(user: User)
                    
                    @Delete
                    suspend fun delete(user: User)
                    
                    @Query("DELETE FROM users")
                    suspend fun deleteAll()
                }
            """.trimIndent()
            
            File(localDir, "UserDao.kt").writeText(userDao)
            
            // Create UserRepository
            val userRepository = """
                package $packageName.data.repository
                
                import androidx.lifecycle.LiveData
                import $packageName.data.local.UserDao
                import $packageName.data.model.User
                
                class UserRepository(private val userDao: UserDao) {
                    
                    val allUsers: LiveData<List<User>> = userDao.getAllUsers()
                    
                    suspend fun insert(user: User) {
                        userDao.insert(user)
                    }
                    
                    suspend fun update(user: User) {
                        userDao.update(user)
                    }
                    
                    suspend fun delete(user: User) {
                        userDao.delete(user)
                    }
                    
                    suspend fun deleteAll() {
                        userDao.deleteAll()
                    }
                    
                    fun getUserById(id: Int): LiveData<User> {
                        return userDao.getUserById(id)
                    }
                }
            """.trimIndent()
            
            File(repositoryDir, "UserRepository.kt").writeText(userRepository)
            
            // Create UserViewModel
            val userViewModel = """
                package $packageName.viewmodel
                
                import androidx.lifecycle.*
                import kotlinx.coroutines.launch
                import $packageName.data.model.User
                import $packageName.data.repository.UserRepository
                
                class UserViewModel(private val repository: UserRepository) : ViewModel() {
                    
                    val allUsers: LiveData<List<User>> = repository.allUsers
                    
                    private val _navigateToUserDetail = MutableLiveData<Int?>()
                    val navigateToUserDetail: LiveData<Int?>
                        get() = _navigateToUserDetail
                    
                    fun insert(user: User) = viewModelScope.launch {
                        repository.insert(user)
                    }
                    
                    fun update(user: User) = viewModelScope.launch {
                        repository.update(user)
                    }
                    
                    fun delete(user: User) = viewModelScope.launch {
                        repository.delete(user)
                    }
                    
                    fun deleteAll() = viewModelScope.launch {
                        repository.deleteAll()
                    }
                    
                    fun onUserClicked(id: Int) {
                        _navigateToUserDetail.value = id
                    }
                    
                    fun onUserDetailNavigated() {
                        _navigateToUserDetail.value = null
                    }
                    
                    fun getUserById(id: Int): LiveData<User> {
                        return repository.getUserById(id)
                    }
                }
                
                class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return UserViewModel(repository) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            """.trimIndent()
            
            File(viewmodelDir, "UserViewModel.kt").writeText(userViewModel)
            
            // Create MainActivity
            val mainActivity = """
                package $packageName.ui
                
                import android.os.Bundle
                import androidx.appcompat.app.AppCompatActivity
                import androidx.navigation.findNavController
                import androidx.navigation.ui.AppBarConfiguration
                import androidx.navigation.ui.navigateUp
                import androidx.navigation.ui.setupActionBarWithNavController
                import $packageName.R
                import $packageName.databinding.ActivityMainBinding
                
                class MainActivity : AppCompatActivity() {
                    
                    private lateinit var appBarConfiguration: AppBarConfiguration
                    private lateinit var binding: ActivityMainBinding
                    
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        
                        binding = ActivityMainBinding.inflate(layoutInflater)
                        setContentView(binding.root)
                        
                        setSupportActionBar(binding.toolbar)
                        
                        val navController = findNavController(R.id.nav_host_fragment_content_main)
                        appBarConfiguration = AppBarConfiguration(navController.graph)
                        setupActionBarWithNavController(navController, appBarConfiguration)
                        
                        binding.fab.setOnClickListener {
                            navController.navigate(R.id.action_UserListFragment_to_AddUserFragment)
                        }
                    }
                    
                    override fun onSupportNavigateUp(): Boolean {
                        val navController = findNavController(R.id.nav_host_fragment_content_main)
                        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
                    }
                }
            """.trimIndent()
            
            File(uiDir, "MainActivity.kt").writeText(mainActivity)
            
            // Create UserListFragment
            val userListFragment = """
                package $packageName.ui
                
                import android.os.Bundle
                import android.view.*
                import androidx.fragment.app.Fragment
                import androidx.fragment.app.viewModels
                import androidx.navigation.fragment.findNavController
                import androidx.recyclerview.widget.LinearLayoutManager
                import $packageName.MyApplication
                import $packageName.R
                import $packageName.databinding.FragmentUserListBinding
                import $packageName.viewmodel.UserViewModel
                import $packageName.viewmodel.UserViewModelFactory
                
                class UserListFragment : Fragment() {
                    
                    private var _binding: FragmentUserListBinding? = null
                    private val binding get() = _binding!!
                    
                    private val userViewModel: UserViewModel by viewModels { 
                        UserViewModelFactory((requireActivity().application as MyApplication).repository)
                    }
                    
                    override fun onCreateView(
                        inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View {
                        _binding = FragmentUserListBinding.inflate(inflater, container, false)
                        return binding.root
                    }
                    
                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        super.onViewCreated(view, savedInstanceState)
                        
                        val adapter = UserListAdapter { user ->
                            userViewModel.onUserClicked(user.id)
                        }
                        
                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        
                        userViewModel.allUsers.observe(viewLifecycleOwner) { users ->
                            users?.let { adapter.submitList(it) }
                            
                            if (users.isEmpty()) {
                                binding.emptyView.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                            } else {
                                binding.emptyView.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                            }
                        }
                        
                        userViewModel.navigateToUserDetail.observe(viewLifecycleOwner) { userId ->
                            userId?.let {
                                val action = UserListFragmentDirections.actionUserListFragmentToUserDetailFragment(userId)
                                findNavController().navigate(action)
                                userViewModel.onUserDetailNavigated()
                            }
                        }
                        
                        setHasOptionsMenu(true)
                    }
                    
                    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                        inflater.inflate(R.menu.menu_main, menu)
                        super.onCreateOptionsMenu(menu, inflater)
                    }
                    
                    override fun onOptionsItemSelected(item: MenuItem): Boolean {
                        return when (item.itemId) {
                            R.id.action_clear_all -> {
                                userViewModel.deleteAll()
                                true
                            }
                            else -> super.onOptionsItemSelected(item)
                        }
                    }
                    
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }
                }
            """.trimIndent()
            
            File(uiDir, "UserListFragment.kt").writeText(userListFragment)
            
            // Create UserListAdapter
            val userListAdapter = """
                package $packageName.ui
                
                import android.view.LayoutInflater
                import android.view.ViewGroup
                import androidx.recyclerview.widget.DiffUtil
                import androidx.recyclerview.widget.ListAdapter
                import androidx.recyclerview.widget.RecyclerView
                import $packageName.data.model.User
                import $packageName.databinding.ItemUserBinding
                
                class UserListAdapter(private val onUserClicked: (User) -> Unit) : 
                    ListAdapter<User, UserListAdapter.UserViewHolder>(UserDiffCallback) {
                    
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                        val binding = ItemUserBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                        return UserViewHolder(binding)
                    }
                    
                    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
                        val user = getItem(position)
                        holder.bind(user)
                    }
                    
                    inner class UserViewHolder(private val binding: ItemUserBinding) : 
                        RecyclerView.ViewHolder(binding.root) {
                        
                        init {
                            binding.root.setOnClickListener {
                                val position = bindingAdapterPosition
                                if (position != RecyclerView.NO_POSITION) {
                                    onUserClicked(getItem(position))
                                }
                            }
                        }
                        
                        fun bind(user: User) {
                            binding.textName.text = user.name
                            binding.textEmail.text = user.email
                            binding.textAge.text = "Age: ${user.age}"
                        }
                    }
                    
                    object UserDiffCallback : DiffUtil.ItemCallback<User>() {
                        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                            return oldItem.id == newItem.id
                        }
                        
                        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                            return oldItem == newItem
                        }
                    }
                }
            """.trimIndent()
            
            File(uiDir, "UserListAdapter.kt").writeText(userListAdapter)
            
            // Create AddUserFragment
            val addUserFragment = """
                package $packageName.ui
                
                import android.os.Bundle
                import android.view.LayoutInflater
                import android.view.View
                import android.view.ViewGroup
                import android.widget.Toast
                import androidx.fragment.app.Fragment
                import androidx.fragment.app.viewModels
                import androidx.navigation.fragment.findNavController
                import $packageName.MyApplication
                import $packageName.R
                import $packageName.data.model.User
                import $packageName.databinding.FragmentAddUserBinding
                import $packageName.viewmodel.UserViewModel
                import $packageName.viewmodel.UserViewModelFactory
                
                class AddUserFragment : Fragment() {
                    
                    private var _binding: FragmentAddUserBinding? = null
                    private val binding get() = _binding!!
                    
                    private val userViewModel: UserViewModel by viewModels { 
                        UserViewModelFactory((requireActivity().application as MyApplication).repository)
                    }
                    
                    override fun onCreateView(
                        inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View {
                        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
                        return binding.root
                    }
                    
                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        super.onViewCreated(view, savedInstanceState)
                        
                        binding.buttonSave.setOnClickListener {
                            if (validateInput()) {
                                val name = binding.editName.text.toString()
                                val email = binding.editEmail.text.toString()
                                val age = binding.editAge.text.toString().toInt()
                                
                                val user = User(name = name, email = email, age = age)
                                userViewModel.insert(user)
                                
                                Toast.makeText(requireContext(), "User saved", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                        }
                    }
                    
                    private fun validateInput(): Boolean {
                        var isValid = true
                        
                        if (binding.editName.text.isNullOrBlank()) {
                            binding.editName.error = "Name is required"
                            isValid = false
                        }
                        
                        if (binding.editEmail.text.isNullOrBlank()) {
                            binding.editEmail.error = "Email is required"
                            isValid = false
                        }
                        
                        if (binding.editAge.text.isNullOrBlank()) {
                            binding.editAge.error = "Age is required"
                            isValid = false
                        } else {
                            try {
                                val age = binding.editAge.text.toString().toInt()
                                if (age <= 0) {
                                    binding.editAge.error = "Age must be positive"
                                    isValid = false
                                }
                            } catch (e: NumberFormatException) {
                                binding.editAge.error = "Age must be a number"
                                isValid = false
                            }
                        }
                        
                        return isValid
                    }
                    
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }
                }
            """.trimIndent()
            
            File(uiDir, "AddUserFragment.kt").writeText(addUserFragment)
            
            // Create UserDetailFragment
            val userDetailFragment = """
                package $packageName.ui
                
                import android.os.Bundle
                import android.view.*
                import androidx.fragment.app.Fragment
                import androidx.fragment.app.viewModels
                import androidx.navigation.fragment.findNavController
                import androidx.navigation.fragment.navArgs
                import $packageName.MyApplication
                import $packageName.R
                import $packageName.databinding.FragmentUserDetailBinding
                import $packageName.viewmodel.UserViewModel
                import $packageName.viewmodel.UserViewModelFactory
                
                class UserDetailFragment : Fragment() {
                    
                    private var _binding: FragmentUserDetailBinding? = null
                    private val binding get() = _binding!!
                    
                    private val args: UserDetailFragmentArgs by navArgs()
                    
                    private val userViewModel: UserViewModel by viewModels { 
                        UserViewModelFactory((requireActivity().application as MyApplication).repository)
                    }
                    
                    override fun onCreateView(
                        inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View {
                        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
                        return binding.root
                    }
                    
                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        super.onViewCreated(view, savedInstanceState)
                        
                        val userId = args.userId
                        
                        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
                            user?.let {
                                binding.textName.text = it.name
                                binding.textEmail.text = it.email
                                binding.textAge.text = "Age: ${it.age}"
                            }
                        }
                        
                        binding.buttonDelete.setOnClickListener {
                            userViewModel.getUserById(userId).value?.let { user ->
                                userViewModel.delete(user)
                                findNavController().navigateUp()
                            }
                        }
                        
                        setHasOptionsMenu(true)
                    }
                    
                    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                        inflater.inflate(R.menu.menu_detail, menu)
                        super.onCreateOptionsMenu(menu, inflater)
                    }
                    
                    override fun onOptionsItemSelected(item: MenuItem): Boolean {
                        return when (item.itemId) {
                            R.id.action_edit -> {
                                // Navigate to edit screen
                                true
                            }
                            else -> super.onOptionsItemSelected(item)
                        }
                    }
                    
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }
                }
            """.trimIndent()
            
            File(uiDir, "UserDetailFragment.kt").writeText(userDetailFragment)
            
            // Create res directory structure
            val resDir = File(mainDir, "res")
            val layoutDir = File(resDir, "layout")
            val valuesDir = File(resDir, "values")
            val menuDir = File(resDir, "menu")
            val navigationDir = File(resDir, "navigation")
            layoutDir.mkdirs()
            valuesDir.mkdirs()
            menuDir.mkdirs()
            navigationDir.mkdirs()
            
            // Create activity_main.xml
            val activityMain = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.coordinatorlayout.widget.CoordinatorLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:context=".ui.MainActivity">
                    
                    <com.google.android.material.appbar.AppBarLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:theme="@style/Theme.MvvmApp.AppBarOverlay">
                        
                        <androidx.appcompat.widget.Toolbar
                            android:id="@+id/toolbar"
                            android:layout_width="match_parent"
                            android:layout_height="?attr/actionBarSize"
                            android:background="?attr/colorPrimary"
                            app:popupTheme="@style/Theme.MvvmApp.PopupOverlay" />
                        
                    </com.google.android.material.appbar.AppBarLayout>
                    
                    <include layout="@layout/content_main" />
                    
                    <com.google.android.material.floatingactionbutton.FloatingActionButton
                        android:id="@+id/fab"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_gravity="bottom|end"
                        android:layout_marginEnd="16dp"
                        android:layout_marginBottom="16dp"
                        app:srcCompat="@android:drawable/ic_input_add" />
                    
                </androidx.coordinatorlayout.widget.CoordinatorLayout>
            """.trimIndent()
            
            File(layoutDir, "activity_main.xml").writeText(activityMain)
            
            // Create content_main.xml
            val contentMain = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">
                    
                    <fragment
                        android:id="@+id/nav_host_fragment_content_main"
                        android:name="androidx.navigation.fragment.NavHostFragment"
                        android:layout_width="0dp"
                        android:layout_height="0dp"
                        app:defaultNavHost="true"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintLeft_toLeftOf="parent"
                        app:layout_constraintRight_toRightOf="parent"
                        app:layout_constraintTop_toTopOf="parent"
                        app:navGraph="@navigation/nav_graph" />
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "content_main.xml").writeText(contentMain)
            
            // Create fragment_user_list.xml
            val fragmentUserList = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:context=".ui.UserListFragment">
                    
                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/recyclerView"
                        android:layout_width="0dp"
                        android:layout_height="0dp"
                        android:padding="8dp"
                        android:clipToPadding="false"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent"
                        tools:listitem="@layout/item_user" />
                    
                    <TextView
                        android:id="@+id/emptyView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="No users found. Add a user with the + button."
                        android:textSize="16sp"
                        android:visibility="gone"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "fragment_user_list.xml").writeText(fragmentUserList)
            
            // Create item_user.xml
            val itemUser = """
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
                            android:id="@+id/textName"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:textSize="18sp"
                            android:textStyle="bold"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent"
                            tools:text="John Doe" />
                        
                        <TextView
                            android:id="@+id/textEmail"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:textSize="14sp"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toBottomOf="@+id/textName"
                            tools:text="john.doe@example.com" />
                        
                        <TextView
                            android:id="@+id/textAge"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:textSize="14sp"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toBottomOf="@+id/textEmail"
                            tools:text="Age: 30" />
                        
                    </androidx.constraintlayout.widget.ConstraintLayout>
                    
                </com.google.android.material.card.MaterialCardView>
            """.trimIndent()
            
            File(layoutDir, "item_user.xml").writeText(itemUser)
            
            // Create fragment_add_user.xml
            val fragmentAddUser = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:padding="16dp"
                    tools:context=".ui.AddUserFragment">
                    
                    <com.google.android.material.textfield.TextInputLayout
                        android:id="@+id/layoutName"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="16dp"
                        android:hint="Name"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent">
                        
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/editName"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textPersonName" />
                            
                    </com.google.android.material.textfield.TextInputLayout>
                    
                    <com.google.android.material.textfield.TextInputLayout
                        android:id="@+id/layoutEmail"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="16dp"
                        android:hint="Email"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@+id/layoutName">
                        
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/editEmail"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textEmailAddress" />
                            
                    </com.google.android.material.textfield.TextInputLayout>
                    
                    <com.google.android.material.textfield.TextInputLayout
                        android:id="@+id/layoutAge"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="16dp"
                        android:hint="Age"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@+id/layoutEmail">
                        
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/editAge"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="number" />
                            
                    </com.google.android.material.textfield.TextInputLayout>
                    
                    <Button
                        android:id="@+id/buttonSave"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="32dp"
                        android:text="Save"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@+id/layoutAge" />
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "fragment_add_user.xml").writeText(fragmentAddUser)
            
            // Create fragment_user_detail.xml
            val fragmentUserDetail = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:padding="16dp"
                    tools:context=".ui.UserDetailFragment">
                    
                    <TextView
                        android:id="@+id/textName"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="32dp"
                        android:textSize="24sp"
                        android:textStyle="bold"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent"
                        tools:text="John Doe" />
                    
                    <TextView
                        android:id="@+id/textEmail"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="16dp"
                        android:textSize="18sp"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@+id/textName"
                        tools:text="john.doe@example.com" />
                    
                    <TextView
                        android:id="@+id/textAge"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="16dp"
                        android:textSize="18sp"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@+id/textEmail"
                        tools:text="Age: 30" />
                    
                    <Button
                        android:id="@+id/buttonDelete"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="32dp"
                        android:text="Delete"
                        android:backgroundTint="@android:color/holo_red_light"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@+id/textAge" />
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "fragment_user_detail.xml").writeText(fragmentUserDetail)
            
            // Create nav_graph.xml
            val navGraph = """
                <?xml version="1.0" encoding="utf-8"?>
                <navigation xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:id="@+id/nav_graph"
                    app:startDestination="@id/UserListFragment">
                    
                    <fragment
                        android:id="@+id/UserListFragment"
                        android:name="$packageName.ui.UserListFragment"
                        android:label="@string/user_list_fragment_label"
                        tools:layout="@layout/fragment_user_list">
                        
                        <action
                            android:id="@+id/action_UserListFragment_to_AddUserFragment"
                            app:destination="@id/AddUserFragment" />
                        <action
                            android:id="@+id/action_UserListFragment_to_UserDetailFragment"
                            app:destination="@id/UserDetailFragment" />
                    </fragment>
                    
                    <fragment
                        android:id="@+id/AddUserFragment"
                        android:name="$packageName.ui.AddUserFragment"
                        android:label="@string/add_user_fragment_label"
                        tools:layout="@layout/fragment_add_user">
                        
                        <action
                            android:id="@+id/action_AddUserFragment_to_UserListFragment"
                            app:destination="@id/UserListFragment"
                            app:popUpTo="@id/UserListFragment"
                            app:popUpToInclusive="true" />
                    </fragment>
                    
                    <fragment
                        android:id="@+id/UserDetailFragment"
                        android:name="$packageName.ui.UserDetailFragment"
                        android:label="@string/user_detail_fragment_label"
                        tools:layout="@layout/fragment_user_detail">
                        
                        <argument
                            android:name="userId"
                            app:argType="integer" />
                        
                        <action
                            android:id="@+id/action_UserDetailFragment_to_UserListFragment"
                            app:destination="@id/UserListFragment"
                            app:popUpTo="@id/UserListFragment"
                            app:popUpToInclusive="true" />
                    </fragment>
                    
                </navigation>
            """.trimIndent()
            
            File(navigationDir, "nav_graph.xml").writeText(navGraph)
            
            // Create menu_main.xml
            val menuMain = """
                <menu xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    tools:context="$packageName.ui.MainActivity">
                    <item
                        android:id="@+id/action_clear_all"
                        android:orderInCategory="100"
                        android:title="@string/action_clear_all"
                        app:showAsAction="never" />
                </menu>
            """.trimIndent()
            
            File(menuDir, "menu_main.xml").writeText(menuMain)
            
            // Create menu_detail.xml
            val menuDetail = """
                <menu xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    tools:context="$packageName.ui.MainActivity">
                    <item
                        android:id="@+id/action_edit"
                        android:orderInCategory="100"
                        android:title="@string/action_edit"
                        android:icon="@android:drawable/ic_menu_edit"
                        app:showAsAction="ifRoom" />
                </menu>
            """.trimIndent()
            
            File(menuDir, "menu_detail.xml").writeText(menuDetail)
            
            // Create strings.xml
            val strings = """
                <resources>
                    <string name="app_name">MVVM App</string>
                    <string name="action_settings">Settings</string>
                    <string name="action_clear_all">Clear All</string>
                    <string name="action_edit">Edit</string>
                    
                    <!-- Strings used for fragments for navigation -->
                    <string name="user_list_fragment_label">User List</string>
                    <string name="add_user_fragment_label">Add User</string>
                    <string name="user_detail_fragment_label">User Details</string>
                    <string name="appbar_scrolling_view_behavior">AppBar Scrolling Behavior</string>
                </resources>
            """.trimIndent()
            
            File(valuesDir, "strings.xml").writeText(strings)
            
            // Create themes.xml
            val themes = """
                <resources>
                    <!-- Base application theme. -->
                    <style name="Theme.MvvmApp" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
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
                    
                    <style name="Theme.MvvmApp.NoActionBar">
                        <item name="windowActionBar">false</item>
                        <item name="windowNoTitle">true</item>
                    </style>
                    
                    <style name="Theme.MvvmApp.AppBarOverlay" parent="ThemeOverlay.AppCompat.Dark.ActionBar" />
                    
                    <style name="Theme.MvvmApp.PopupOverlay" parent="ThemeOverlay.AppCompat.Light" />
                </resources>
            """.trimIndent()
            
            File(valuesDir, "themes.xml").writeText(themes)
            
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