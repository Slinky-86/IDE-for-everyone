package com.anyoneide.app.core

import java.io.File

/**
 * Class for creating project templates
 */
class ProjectTemplates(private val context: android.content.Context) {
    
    /**
     * Create a basic Android app template
     */
    fun createAndroidAppTemplate(projectDir: File, packageName: String) {
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
            }
            
            dependencies {
                implementation 'androidx.core:core-ktx:1.12.0'
                implementation 'androidx.appcompat:appcompat:1.6.1'
                implementation 'com.google.android.material:material:1.11.0'
                implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
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
            
            class MainActivity : AppCompatActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContentView(R.layout.activity_main)
                }
            }
        """.trimIndent()
        
        File(javaDir, "MainActivity.kt").writeText(mainActivity)
        
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
                tools:context=".MainActivity">
                
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Hello World!"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />
                
            </androidx.constraintlayout.widget.ConstraintLayout>
        """.trimIndent()
        
        File(layoutDir, "activity_main.xml").writeText(activityMain)
        
        // Create strings.xml
        val strings = """
            <resources>
                <string name="app_name">My App</string>
            </resources>
        """.trimIndent()
        
        File(valuesDir, "strings.xml").writeText(strings)
    }
    
    /**
     * Create a Rust Android library template
     */
    fun createRustAndroidLibraryTemplate(projectDir: File, packageName: String) {
        // Create project structure
        createBasicProjectStructure(projectDir, packageName)
        
        // Create Rust library directory
        val rustDir = File(projectDir, "rust-lib")
        rustDir.mkdirs()
        
        // Create Cargo.toml
        val cargoToml = """
            [package]
            name = "rust-lib"
            version = "0.1.0"
            edition = "2021"
            authors = ["Anyone IDE User"]
            
            [lib]
            crate-type = ["cdylib", "staticlib", "rlib"]
            
            [dependencies]
            jni = { version = "0.21.1", features = ["invocation"] }
        """.trimIndent()
        
        File(rustDir, "Cargo.toml").writeText(cargoToml)
        
        // Create src directory
        val srcDir = File(rustDir, "src")
        srcDir.mkdirs()
        
        // Create lib.rs with JNI exports
        val libRs = """
            use jni::JNIEnv;
            use jni::objects::{JClass, JString};
            use jni::sys::jstring;
            
            #[no_mangle]
            pub extern "C" fn Java_${packageName.replace(".", "_")}_RustLib_getGreeting(env: JNIEnv, _class: JClass) -> jstring {
                let output = env.new_string("Hello from Rust!").expect("Couldn't create Java string!");
                output.into_raw()
            }
            
            #[no_mangle]
            pub extern "C" fn Java_${packageName.replace(".", "_")}_RustLib_processString(env: JNIEnv, _class: JClass, input: JString) -> jstring {
                let input: String = env.get_string(input).expect("Couldn't get Java string!").into();
                let output = format!("Rust processed: {}", input);
                let output = env.new_string(output).expect("Couldn't create Java string!");
                output.into_raw()
            }
        """.trimIndent()
        
        File(srcDir, "lib.rs").writeText(libRs)
        
        // Create .cargo directory and config.toml
        val cargoConfigDir = File(rustDir, ".cargo")
        cargoConfigDir.mkdirs()
        
        val cargoConfig = """
            [target.aarch64-linux-android]
            ar = "aarch64-linux-android-ar"
            linker = "aarch64-linux-android-clang"
            
            [target.armv7-linux-androideabi]
            ar = "arm-linux-androideabi-ar"
            linker = "arm-linux-androideabi-clang"
            
            [target.i686-linux-android]
            ar = "i686-linux-android-ar"
            linker = "i686-linux-android-clang"
            
            [target.x86_64-linux-android]
            ar = "x86_64-linux-android-ar"
            linker = "x86_64-linux-android-clang"
        """.trimIndent()
        
        File(cargoConfigDir, "config.toml").writeText(cargoConfig)
        
        // Create Android app module
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
                    
                    ndk {
                        abiFilters 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'
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
                
                // Load native libraries from the Rust project
                sourceSets {
                    main {
                        jniLibs.srcDirs = ['../rust-lib/target/debug', '../rust-lib/target/release']
                    }
                }
                
                // Task to build Rust library before building Android app
                tasks.whenTaskAdded { task ->
                    if (task.name == 'preBuild') {
                        task.dependsOn 'buildRustLibrary'
                    }
                }
            }
            
            // Task to build Rust library
            task buildRustLibrary(type: Exec) {
                workingDir '../rust-lib'
                commandLine 'cargo', 'build', '--release'
            }
            
            dependencies {
                implementation 'androidx.core:core-ktx:1.12.0'
                implementation 'androidx.appcompat:appcompat:1.6.1'
                implementation 'com.google.android.material:material:1.11.0'
                implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
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
        
        // Create RustLib.java
        val rustLib = """
            package $packageName;
            
            import androidx.annotation.NonNull;
            
            /**
             * Java wrapper for Rust library
             */
            public class RustLib {
                
                static {
                    System.loadLibrary("rust-lib");
                }
                
                /**
                 * Example method that calls into Rust code
                 * @return String returned from Rust
                 */
                @NonNull
                public static native String getGreeting();
                
                /**
                 * Example method that passes data to Rust
                 * @param input String to process
                 * @return Processed string from Rust
                 */
                @NonNull
                public static native String processString(@NonNull String input);
            }
        """.trimIndent()
        
        File(javaDir, "RustLib.java").writeText(rustLib)
        
        // Create MainActivity.kt
        val mainActivity = """
            package $packageName
            
            import android.os.Bundle
            import android.widget.TextView
            import androidx.appcompat.app.AppCompatActivity
            
            class MainActivity : AppCompatActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContentView(R.layout.activity_main)
                    
                    // Get greeting from Rust
                    val textView = findViewById<TextView>(R.id.textView)
                    textView.text = RustLib.getGreeting()
                    
                    // Process a string with Rust
                    val processedText = RustLib.processString("Hello from Kotlin!")
                    findViewById<TextView>(R.id.processedTextView).text = processedText
                }
            }
        """.trimIndent()
        
        File(javaDir, "MainActivity.kt").writeText(mainActivity)
        
        // Create res directory structure
        val resDir = File(mainDir, "res")
        val layoutDir = File(resDir, "layout")
        val valuesDir = File(resDir, "values")
        layoutDir.mkdirs()
        valuesDir.mkdirs()
        
        // Create activity_main.xml
        val activityMain = """
            <?xml version="1.0" encoding="utf-8"?>
            <LinearLayout 
                xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical"
                android:gravity="center"
                android:padding="16dp">
                
                <TextView
                    android:id="@+id/textView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textSize="24sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="16dp" />
                
                <TextView
                    android:id="@+id/processedTextView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textSize="18sp" />
                
            </LinearLayout>
        """.trimIndent()
        
        File(layoutDir, "activity_main.xml").writeText(activityMain)
        
        // Create strings.xml
        val strings = """
            <resources>
                <string name="app_name">Rust Android App</string>
            </resources>
        """.trimIndent()
        
        File(valuesDir, "strings.xml").writeText(strings)
        
        // Create settings.gradle
        val settingsGradle = """
            rootProject.name = "${projectDir.name}"
            include ':app'
        """.trimIndent()
        
        File(projectDir, "settings.gradle").writeText(settingsGradle)
    }
    
    /**
     * Create a basic project structure
     */
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
            
            # Rust
            /target
            Cargo.lock
        """.trimIndent()
        
        File(projectDir, ".gitignore").writeText(gitignore)
    }
    
    /**
     * Create an Android library template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createAndroidLibraryProject(projectDir: File, projectName: String) {
        // Implementation omitted for brevity
    }
    
    /**
     * Create a Java library template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createJavaLibraryProject(projectDir: File, projectName: String) {
        // Implementation omitted for brevity
    }
    
    /**
     * Create a Kotlin Multiplatform template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createKotlinMultiplatformProject(projectDir: File, projectName: String) {
        // Implementation omitted for brevity
    }
    
    /**
     * Create a Compose app template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createComposeAppTemplate(projectDir: File, packageName: String) {
        // Implementation omitted for brevity
    }
    
    /**
     * Create an MVVM app template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createMvvmAppTemplate(projectDir: File, packageName: String) {
        // Implementation omitted for brevity
    }
    
    /**
     * Create a REST API client template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createRestApiClientTemplate(projectDir: File, packageName: String) {
        // Implementation omitted for brevity
    }
    
    /**
     * Create a 2D game template
     */
    fun create2DGameTemplate(projectDir: File, packageName: String) {
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
                        android:exported="true"
                        android:screenOrientation="portrait">
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
            import $packageName.databinding.ActivityMainBinding
            
            class MainActivity : AppCompatActivity() {
                
                private lateinit var binding: ActivityMainBinding
                private lateinit var gameView: GameView
                
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    binding = ActivityMainBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    
                    gameView = binding.gameView
                }
                
                override fun onResume() {
                    super.onResume()
                    gameView.resume()
                }
                
                override fun onPause() {
                    super.onPause()
                    gameView.pause()
                }
            }
        """.trimIndent()
        
        File(javaDir, "MainActivity.kt").writeText(mainActivity)
        
        // Create GameView.kt
        val gameView = """
            package $packageName
            
            import android.content.Context
            import android.graphics.Canvas
            import android.graphics.Color
            import android.graphics.Paint
            import android.util.AttributeSet
            import android.view.MotionEvent
            import android.view.SurfaceHolder
            import android.view.SurfaceView
            
            class GameView @JvmOverloads constructor(
                context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0
            ) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {
                
                private val gameThread: GameThread
                private val paint = Paint()
                
                // Game objects
                private var player = GameObject(100f, 100f, 50f, Color.BLUE)
                private var enemy = GameObject(500f, 500f, 50f, Color.RED)
                private var score = 0
                
                init {
                    holder.addCallback(this)
                    gameThread = GameThread(holder, this)
                    isFocusable = true
                    
                    // Initialize paint
                    paint.color = Color.WHITE
                    paint.textSize = 60f
                }
                
                override fun surfaceCreated(holder: SurfaceHolder) {
                    gameThread.setRunning(true)
                    gameThread.start()
                }
                
                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    // Reset game objects based on new dimensions
                    player = GameObject(width / 4f, height / 2f, 50f, Color.BLUE)
                    enemy = GameObject(width * 3 / 4f, height / 2f, 50f, Color.RED)
                }
                
                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    var retry = true
                    gameThread.setRunning(false)
                    while (retry) {
                        try {
                            gameThread.join()
                            retry = false
                        } catch (e: InterruptedException) {
                            // Try again
                        }
                    }
                }
                
                fun update() {
                    // Update game logic here
                    
                    // Move enemy
                    if (player.x > enemy.x) enemy.x += 2f
                    if (player.x < enemy.x) enemy.x -= 2f
                    if (player.y > enemy.y) enemy.y += 2f
                    if (player.y < enemy.y) enemy.y -= 2f
                    
                    // Check collision
                    val distance = Math.sqrt(
                        Math.pow((player.x - enemy.x).toDouble(), 2.0) +
                        Math.pow((player.y - enemy.y).toDouble(), 2.0)
                    ).toFloat()
                    
                    if (distance < player.radius + enemy.radius) {
                        // Collision detected
                        score--
                        // Reset enemy position
                        enemy.x = width * 0.8f
                        enemy.y = height * 0.8f
                    }
                }
                
                fun draw(canvas: Canvas) {
                    // Clear the screen
                    canvas.drawColor(Color.BLACK)
                    
                    // Draw game objects
                    player.draw(canvas)
                    enemy.draw(canvas)
                    
                    // Draw score
                    canvas.drawText("Score: $score", 50f, 80f, paint)
                }
                
                override fun onTouchEvent(event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            player.x = event.x
                            player.y = event.y
                            score++
                        }
                    }
                    return true
                }
                
                fun pause() {
                    gameThread.setRunning(false)
                }
                
                fun resume() {
                    if (!gameThread.isAlive) {
                        gameThread.setRunning(true)
                        gameThread.start()
                    }
                }
                
                // Game object class
                inner class GameObject(
                    var x: Float,
                    var y: Float,
                    val radius: Float,
                    val color: Int
                ) {
                    private val paint = Paint()
                    
                    init {
                        paint.color = color
                    }
                    
                    fun draw(canvas: Canvas) {
                        canvas.drawCircle(x, y, radius, paint)
                    }
                }
                
                // Game thread class
                inner class GameThread(
                    private val surfaceHolder: SurfaceHolder,
                    private val gameView: GameView
                ) : Thread() {
                    
                    private var running = false
                    private val targetFPS = 60
                    private val targetTime = 1000 / targetFPS
                    
                    fun setRunning(isRunning: Boolean) {
                        running = isRunning
                    }
                    
                    override fun run() {
                        var startTime: Long
                        var timeMillis: Long
                        var waitTime: Long
                        
                        while (running) {
                            startTime = System.nanoTime()
                            var canvas: Canvas? = null
                            
                            try {
                                canvas = surfaceHolder.lockCanvas()
                                synchronized(surfaceHolder) {
                                    gameView.update()
                                    gameView.draw(canvas)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                if (canvas != null) {
                                    try {
                                        surfaceHolder.unlockCanvasAndPost(canvas)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            
                            timeMillis = (System.nanoTime() - startTime) / 1000000
                            waitTime = targetTime - timeMillis
                            
                            try {
                                if (waitTime > 0) {
                                    sleep(waitTime)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        """.trimIndent()
        
        File(javaDir, "GameView.kt").writeText(gameView)
        
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
                tools:context=".MainActivity">
                
                <$packageName.GameView
                    android:id="@+id/gameView"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />
                
            </androidx.constraintlayout.widget.ConstraintLayout>
        """.trimIndent()
        
        File(layoutDir, "activity_main.xml").writeText(activityMain)
        
        // Create strings.xml
        val strings = """
            <resources>
                <string name="app_name">2D Game</string>
            </resources>
        """.trimIndent()
        
        File(valuesDir, "strings.xml").writeText(strings)
    }
    
    /**
     * Create an Empty Activity template
     */
    @Suppress("UNUSED_PARAMETER")
    fun createEmptyActivityTemplate(projectDir: File, packageName: String) {
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
            import $packageName.databinding.ActivityMainBinding
            
            class MainActivity : AppCompatActivity() {
                
                private lateinit var binding: ActivityMainBinding
                
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    binding = ActivityMainBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                }
            }
        """.trimIndent()
        
        File(javaDir, "MainActivity.kt").writeText(mainActivity)
        
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
                tools:context=".MainActivity">
                
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Hello World!"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />
                
            </androidx.constraintlayout.widget.ConstraintLayout>
        """.trimIndent()
        
        File(layoutDir, "activity_main.xml").writeText(activityMain)
        
        // Create strings.xml
        val strings = """
            <resources>
                <string name="app_name">My App</string>
            </resources>
        """.trimIndent()
        
        File(valuesDir, "strings.xml").writeText(strings)
    }
}