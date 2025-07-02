package com.anyoneide.app.templates

import com.anyoneide.app.core.ProjectTemplate
import java.io.File

/**
 * Basic Android Application template
 */
class AndroidBasicTemplate : ProjectTemplate {
    
    override val id = "android_basic"
    override val name = "Basic Android App"
    override val description = "A simple Android application with basic navigation and Material Design components."
    override val category = "Android"
    override val features = listOf("Material Design 3", "Navigation Component", "ViewBinding", "Kotlin")
    override val difficulty = "Beginner"
    override val estimatedTime = "30 min"
    
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
                    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.5'
                    implementation 'androidx.navigation:navigation-ui-ktx:2.7.5'
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
                        android:allowBackup="true"
                        android:icon="@mipmap/ic_launcher"
                        android:label="@string/app_name"
                        android:roundIcon="@mipmap/ic_launcher_round"
                        android:supportsRtl="true"
                        android:theme="@style/Theme.MyApp">
                        
                        <activity
                            android:name=".MainActivity"
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
            
            // Create Java directory structure based on package name
            val packagePath = packageName.replace(".", "/")
            val javaDir = File(mainDir, "java/$packagePath")
            javaDir.mkdirs()
            
            // Create MainActivity.kt
            val mainActivity = """
                package $packageName
                
                import android.os.Bundle
                import androidx.appcompat.app.AppCompatActivity
                import androidx.navigation.findNavController
                import androidx.navigation.ui.AppBarConfiguration
                import androidx.navigation.ui.navigateUp
                import androidx.navigation.ui.setupActionBarWithNavController
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
                        
                        binding.fab.setOnClickListener { view ->
                            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                        }
                    }
                    
                    override fun onSupportNavigateUp(): Boolean {
                        val navController = findNavController(R.id.nav_host_fragment_content_main)
                        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
                    }
                }
            """.trimIndent()
            
            File(javaDir, "MainActivity.kt").writeText(mainActivity)
            
            // Create FirstFragment.kt
            val firstFragment = """
                package $packageName
                
                import android.os.Bundle
                import androidx.fragment.app.Fragment
                import android.view.LayoutInflater
                import android.view.View
                import android.view.ViewGroup
                import $packageName.databinding.FragmentFirstBinding
                
                class FirstFragment : Fragment() {
                    
                    private var _binding: FragmentFirstBinding? = null
                    private val binding get() = _binding!!
                    
                    override fun onCreateView(
                        inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View {
                        _binding = FragmentFirstBinding.inflate(inflater, container, false)
                        return binding.root
                    }
                    
                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        super.onViewCreated(view, savedInstanceState)
                        
                        binding.buttonFirst.setOnClickListener {
                            // Navigate to second fragment
                        }
                    }
                    
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }
                }
            """.trimIndent()
            
            File(javaDir, "FirstFragment.kt").writeText(firstFragment)
            
            // Create SecondFragment.kt
            val secondFragment = """
                package $packageName
                
                import android.os.Bundle
                import androidx.fragment.app.Fragment
                import android.view.LayoutInflater
                import android.view.View
                import android.view.ViewGroup
                import $packageName.databinding.FragmentSecondBinding
                
                class SecondFragment : Fragment() {
                    
                    private var _binding: FragmentSecondBinding? = null
                    private val binding get() = _binding!!
                    
                    override fun onCreateView(
                        inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View {
                        _binding = FragmentSecondBinding.inflate(inflater, container, false)
                        return binding.root
                    }
                    
                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        super.onViewCreated(view, savedInstanceState)
                        
                        binding.buttonSecond.setOnClickListener {
                            // Navigate back to first fragment
                        }
                    }
                    
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }
                }
            """.trimIndent()
            
            File(javaDir, "SecondFragment.kt").writeText(secondFragment)
            
            // Create res directory structure
            val resDir = File(mainDir, "res")
            val layoutDir = File(resDir, "layout")
            val valuesDir = File(resDir, "values")
            val navigationDir = File(resDir, "navigation")
            layoutDir.mkdirs()
            valuesDir.mkdirs()
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
                    tools:context=".MainActivity">
                    
                    <com.google.android.material.appbar.AppBarLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:theme="@style/Theme.MyApp.AppBarOverlay">
                        
                        <androidx.appcompat.widget.Toolbar
                            android:id="@+id/toolbar"
                            android:layout_width="match_parent"
                            android:layout_height="?attr/actionBarSize"
                            android:background="?attr/colorPrimary"
                            app:popupTheme="@style/Theme.MyApp.PopupOverlay" />
                        
                    </com.google.android.material.appbar.AppBarLayout>
                    
                    <include layout="@layout/content_main" />
                    
                    <com.google.android.material.floatingactionbutton.FloatingActionButton
                        android:id="@+id/fab"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_gravity="bottom|end"
                        android:layout_marginEnd="16dp"
                        android:layout_marginBottom="16dp"
                        app:srcCompat="@android:drawable/ic_dialog_email" />
                    
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
            
            // Create fragment_first.xml
            val fragmentFirst = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:context=".FirstFragment">
                    
                    <TextView
                        android:id="@+id/textview_first"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@string/hello_first_fragment"
                        app:layout_constraintBottom_toTopOf="@id/button_first"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />
                    
                    <Button
                        android:id="@+id/button_first"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@string/next"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@id/textview_first" />
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "fragment_first.xml").writeText(fragmentFirst)
            
            // Create fragment_second.xml
            val fragmentSecond = """
                <?xml version="1.0" encoding="utf-8"?>
                <androidx.constraintlayout.widget.ConstraintLayout 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:context=".SecondFragment">
                    
                    <TextView
                        android:id="@+id/textview_second"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@string/hello_second_fragment"
                        app:layout_constraintBottom_toTopOf="@id/button_second"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />
                    
                    <Button
                        android:id="@+id/button_second"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="@string/previous"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@id/textview_second" />
                    
                </androidx.constraintlayout.widget.ConstraintLayout>
            """.trimIndent()
            
            File(layoutDir, "fragment_second.xml").writeText(fragmentSecond)
            
            // Create nav_graph.xml
            val navGraph = """
                <?xml version="1.0" encoding="utf-8"?>
                <navigation xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:id="@+id/nav_graph"
                    app:startDestination="@id/FirstFragment">
                    
                    <fragment
                        android:id="@+id/FirstFragment"
                        android:name="$packageName.FirstFragment"
                        android:label="@string/first_fragment_label"
                        tools:layout="@layout/fragment_first">
                        
                        <action
                            android:id="@+id/action_FirstFragment_to_SecondFragment"
                            app:destination="@id/SecondFragment" />
                    </fragment>
                    
                    <fragment
                        android:id="@+id/SecondFragment"
                        android:name="$packageName.SecondFragment"
                        android:label="@string/second_fragment_label"
                        tools:layout="@layout/fragment_second">
                        
                        <action
                            android:id="@+id/action_SecondFragment_to_FirstFragment"
                            app:destination="@id/FirstFragment" />
                    </fragment>
                    
                </navigation>
            """.trimIndent()
            
            File(navigationDir, "nav_graph.xml").writeText(navGraph)
            
            // Create strings.xml
            val strings = """
                <resources>
                    <string name="app_name">My App</string>
                    <string name="action_settings">Settings</string>
                    <!-- Strings used for fragments for navigation -->
                    <string name="first_fragment_label">First Fragment</string>
                    <string name="second_fragment_label">Second Fragment</string>
                    <string name="next">Next</string>
                    <string name="previous">Previous</string>
                    
                    <string name="hello_first_fragment">Hello first fragment</string>
                    <string name="hello_second_fragment">Hello second fragment</string>
                    <string name="appbar_scrolling_view_behavior">AppBar Scrolling Behavior</string>
                </resources>
            """.trimIndent()
            
            File(valuesDir, "strings.xml").writeText(strings)
            
            // Create themes.xml
            val themes = """
                <resources>
                    <!-- Base application theme. -->
                    <style name="Theme.MyApp" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
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
                    
                    <style name="Theme.MyApp.NoActionBar">
                        <item name="windowActionBar">false</item>
                        <item name="windowNoTitle">true</item>
                    </style>
                    
                    <style name="Theme.MyApp.AppBarOverlay" parent="ThemeOverlay.AppCompat.Dark.ActionBar" />
                    
                    <style name="Theme.MyApp.PopupOverlay" parent="ThemeOverlay.AppCompat.Light" />
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