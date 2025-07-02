package com.anyoneide.app.templates

import com.anyoneide.app.core.ProjectTemplate
import java.io.File

/**
 * Jetpack Compose Application template
 */
class ComposeAppTemplate : ProjectTemplate {
    
    override val id = "compose_app"
    override val name = "Jetpack Compose App"
    override val description = "Modern Android app built with Jetpack Compose for declarative UI development."
    override val category = "Compose"
    override val features = listOf("Jetpack Compose", "Material Design 3", "State Management", "Navigation")
    override val difficulty = "Intermediate"
    override val estimatedTime = "45 min"
    
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
                        vectorDrawables {
                            useSupportLibrary true
                        }
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
                        compose true
                    }
                    
                    composeOptions {
                        kotlinCompilerExtensionVersion '1.5.4'
                    }
                    
                    packaging {
                        resources {
                            excludes += '/META-INF/{AL2.0,LGPL2.1}'
                        }
                    }
                }
                
                dependencies {
                    implementation 'androidx.core:core-ktx:1.12.0'
                    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
                    implementation 'androidx.activity:activity-compose:1.8.2'
                    implementation platform('androidx.compose:compose-bom:2024.02.00')
                    implementation 'androidx.compose.ui:ui'
                    implementation 'androidx.compose.ui:ui-graphics'
                    implementation 'androidx.compose.ui:ui-tooling-preview'
                    implementation 'androidx.compose.material3:material3'
                    implementation 'androidx.navigation:navigation-compose:2.7.5'
                    testImplementation 'junit:junit:4.13.2'
                    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
                    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
                    androidTestImplementation platform('androidx.compose:compose-bom:2024.02.00')
                    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
                    debugImplementation 'androidx.compose.ui:ui-tooling'
                    debugImplementation 'androidx.compose.ui:ui-test-manifest'
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
                        android:theme="@style/Theme.ComposeApp">
                        
                        <activity
                            android:name=".MainActivity"
                            android:exported="true"
                            android:theme="@style/Theme.ComposeApp">
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
                import androidx.activity.ComponentActivity
                import androidx.activity.compose.setContent
                import androidx.compose.foundation.layout.fillMaxSize
                import androidx.compose.material3.MaterialTheme
                import androidx.compose.material3.Surface
                import androidx.compose.runtime.Composable
                import androidx.compose.ui.Modifier
                import androidx.navigation.compose.NavHost
                import androidx.navigation.compose.composable
                import androidx.navigation.compose.rememberNavController
                import $packageName.ui.screens.HomeScreen
                import $packageName.ui.screens.DetailScreen
                import $packageName.ui.theme.ComposeAppTheme
                
                class MainActivity : ComponentActivity() {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        setContent {
                            ComposeAppTheme {
                                // A surface container using the 'background' color from the theme
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    AppNavigation()
                                }
                            }
                        }
                    }
                }
                
                @Composable
                fun AppNavigation() {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(onNavigateToDetail = { 
                                navController.navigate("detail") 
                            })
                        }
                        composable("detail") {
                            DetailScreen(onNavigateBack = { 
                                navController.popBackStack() 
                            })
                        }
                    }
                }
            """.trimIndent()
            
            File(javaDir, "MainActivity.kt").writeText(mainActivity)
            
            // Create ui directory structure
            val uiDir = File(javaDir, "ui")
            val screensDir = File(uiDir, "screens")
            val themeDir = File(uiDir, "theme")
            val componentsDir = File(uiDir, "components")
            screensDir.mkdirs()
            themeDir.mkdirs()
            componentsDir.mkdirs()
            
            // Create HomeScreen.kt
            val homeScreen = """
                package $packageName.ui.screens
                
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.lazy.LazyColumn
                import androidx.compose.foundation.lazy.items
                import androidx.compose.material.icons.Icons
                import androidx.compose.material.icons.filled.Add
                import androidx.compose.material3.*
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.unit.dp
                import $packageName.ui.components.ItemCard
                
                @OptIn(ExperimentalMaterial3Api::class)
                @Composable
                fun HomeScreen(onNavigateToDetail: () -> Unit) {
                    val items = remember { List(20) { "Item #${'$'}it" } }
                    
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Compose App") }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(onClick = onNavigateToDetail) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    ) { padding ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    text = "Welcome to Compose!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            
                            items(items) { item ->
                                ItemCard(
                                    title = item,
                                    description = "This is a sample item in our list",
                                    onClick = onNavigateToDetail
                                )
                            }
                        }
                    }
                }
            """.trimIndent()
            
            File(screensDir, "HomeScreen.kt").writeText(homeScreen)
            
            // Create DetailScreen.kt
            val detailScreen = """
                package $packageName.ui.screens
                
                import androidx.compose.foundation.layout.*
                import androidx.compose.material.icons.Icons
                import androidx.compose.material.icons.filled.ArrowBack
                import androidx.compose.material3.*
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.unit.dp
                
                @OptIn(ExperimentalMaterial3Api::class)
                @Composable
                fun DetailScreen(onNavigateBack: () -> Unit) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Detail") },
                                navigationIcon = {
                                    IconButton(onClick = onNavigateBack) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Detail Screen",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "This is a detail screen in Jetpack Compose",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(onClick = onNavigateBack) {
                                Text("Go Back")
                            }
                        }
                    }
                }
            """.trimIndent()
            
            File(screensDir, "DetailScreen.kt").writeText(detailScreen)
            
            // Create ItemCard.kt
            val itemCard = """
                package $packageName.ui.components
                
                import androidx.compose.foundation.layout.*
                import androidx.compose.material3.*
                import androidx.compose.runtime.*
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.unit.dp
                
                @OptIn(ExperimentalMaterial3Api::class)
                @Composable
                fun ItemCard(
                    title: String,
                    description: String,
                    onClick: () -> Unit
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onClick,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            """.trimIndent()
            
            File(componentsDir, "ItemCard.kt").writeText(itemCard)
            
            // Create Theme.kt
            val theme = """
                package $packageName.ui.theme
                
                import android.app.Activity
                import android.os.Build
                import androidx.compose.foundation.isSystemInDarkTheme
                import androidx.compose.material3.MaterialTheme
                import androidx.compose.material3.darkColorScheme
                import androidx.compose.material3.dynamicDarkColorScheme
                import androidx.compose.material3.dynamicLightColorScheme
                import androidx.compose.material3.lightColorScheme
                import androidx.compose.runtime.Composable
                import androidx.compose.runtime.SideEffect
                import androidx.compose.ui.graphics.toArgb
                import androidx.compose.ui.platform.LocalContext
                import androidx.compose.ui.platform.LocalView
                import androidx.core.view.WindowCompat
                
                private val DarkColorScheme = darkColorScheme(
                    primary = Purple80,
                    secondary = PurpleGrey80,
                    tertiary = Pink80
                )
                
                private val LightColorScheme = lightColorScheme(
                    primary = Purple40,
                    secondary = PurpleGrey40,
                    tertiary = Pink40
                )
                
                @Composable
                fun ComposeAppTheme(
                    darkTheme: Boolean = isSystemInDarkTheme(),
                    // Dynamic color is available on Android 12+
                    dynamicColor: Boolean = true,
                    content: @Composable () -> Unit
                ) {
                    val colorScheme = when {
                        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                            val context = LocalContext.current
                            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                        }
                        darkTheme -> DarkColorScheme
                        else -> LightColorScheme
                    }
                    val view = LocalView.current
                    if (!view.isInEditMode) {
                        SideEffect {
                            val window = (view.context as Activity).window
                            window.statusBarColor = colorScheme.primary.toArgb()
                            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
                        }
                    }
                
                    MaterialTheme(
                        colorScheme = colorScheme,
                        typography = Typography,
                        content = content
                    )
                }
            """.trimIndent()
            
            File(themeDir, "Theme.kt").writeText(theme)
            
            // Create Color.kt
            val color = """
                package $packageName.ui.theme
                
                import androidx.compose.ui.graphics.Color
                
                val Purple80 = Color(0xFFD0BCFF)
                val PurpleGrey80 = Color(0xFFCCC2DC)
                val Pink80 = Color(0xFFEFB8C8)
                
                val Purple40 = Color(0xFF6650a4)
                val PurpleGrey40 = Color(0xFF625b71)
                val Pink40 = Color(0xFF7D5260)
            """.trimIndent()
            
            File(themeDir, "Color.kt").writeText(color)
            
            // Create Type.kt
            val type = """
                package $packageName.ui.theme
                
                import androidx.compose.material3.Typography
                import androidx.compose.ui.text.TextStyle
                import androidx.compose.ui.text.font.FontFamily
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.unit.sp
                
                // Set of Material typography styles to start with
                val Typography = Typography(
                    bodyLarge = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.5.sp
                    ),
                    titleLarge = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp
                    ),
                    labelSmall = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            """.trimIndent()
            
            File(themeDir, "Type.kt").writeText(type)
            
            // Create res directory structure
            val resDir = File(mainDir, "res")
            val valuesDir = File(resDir, "values")
            valuesDir.mkdirs()
            
            // Create strings.xml
            val strings = """
                <resources>
                    <string name="app_name">Compose App</string>
                </resources>
            """.trimIndent()
            
            File(valuesDir, "strings.xml").writeText(strings)
            
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