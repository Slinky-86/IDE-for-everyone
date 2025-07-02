package com.anyoneide.app.templates

import com.anyoneide.app.core.ProjectTemplate
import java.io.File

/**
 * 2D Game Android Application template
 */
class Game2DTemplate : ProjectTemplate {
    
    override val id = "game_2d"
    override val name = "2D Game"
    override val description = "Simple 2D game using Android Canvas and custom views for game development."
    override val category = "Games"
    override val features = listOf("Custom Views", "Canvas Drawing", "Touch Input", "Game Loop")
    override val difficulty = "Intermediate"
    override val estimatedTime = "90 min"
    
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