package com.anyoneide.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyoneide.app.core.*
import com.anyoneide.app.data.DataModule
import com.anyoneide.app.data.model.BookmarkedCommand
import com.anyoneide.app.data.repository.BookmarkedCommandRepository
import com.anyoneide.app.model.*
import com.anyoneide.app.ui.components.ProjectTemplateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class EnhancedMainViewModel(private val context: Context) : ViewModel() {
    
    // Core managers
    private val fileManager = FileManager(context)
    private val projectManager = ProjectManager(context)
    private val buildManager = BuildManager(context)
    private val editorManager = EditorManager(context)
    private val terminalManager = TerminalManager(context)
    private val gitIntegration = GitIntegration(context)
    private val settingsManager = SettingsManager(context)
    private val themeManager = ThemeManager(context)
    private val languageSupport = LanguageSupport(context)
    private val geminiApiService = GeminiApiService(context)
    private val rustBuildManager = RustBuildManager(context)
    private val rustNativeBuildManager = RustNativeBuildManager(context)
    private val rustTerminalManager = RustTerminalManager(context)
    private val gradleBuildManager = GradleBuildManager(context)
    private val gradleFileModifier = GradleFileModifier(context)
    private val androidPackageManager = AndroidPackageManager(context)
    private val rootManager = RootManager(context)
    private val pluginManager = PluginManager(context)
    
    // Repositories
    private val bookmarkedCommandRepository = DataModule.provideBookmarkedCommandRepository(context)
    
    // UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Root access requested flag
    var isRootAccessRequested = false
        private set
    
    init {
        // Load settings
        _uiState.update { it.copy(settings = settingsManager.settings.value) }
        
        // Load themes
        _uiState.update { it.copy(
            availableThemes = themeManager.availableThemes.value,
            currentTheme = themeManager.currentTheme.value
        ) }
        
        // Load languages
        _uiState.update { it.copy(
            availableLanguages = languageSupport.getAllLanguages()
        ) }
        
        // Load plugins
        viewModelScope.launch {
            _uiState.update { it.copy(
                installedPlugins = pluginManager.installedPlugins.value,
                availablePlugins = pluginManager.availablePlugins.value
            ) }
        }
        
        // Check if native build system is available
        viewModelScope.launch {
            val isAvailable = rustNativeBuildManager.isNativeBuildSystemAvailable()
            val rustVersion = rustNativeBuildManager.getRustVersion()
            val buildSystemStatus = rustNativeBuildManager.getBuildSystemStatus()
            
            _uiState.update { it.copy(
                isNativeBuildSystemAvailable = isAvailable,
                rustVersion = rustVersion,
                nativeBuildSystemStatus = buildSystemStatus
            ) }
        }
        
        // Load bookmarked commands
        viewModelScope.launch {
            bookmarkedCommandRepository.getBookmarkedCommands().onSuccess { commands ->
                _uiState.update { it.copy(bookmarkedCommands = commands) }
            }
        }
        
        // Subscribe to settings changes
        viewModelScope.launch {
            settingsManager.settings.collect { settings ->
                _uiState.update { it.copy(
                    settings = settings,
                    isDarkTheme = settings.isDarkTheme,
                    showLeftPanel = settings.showProjectExplorer,
                    showRightPanel = settings.showToolWindows,
                    showBottomPanel = settings.showBottomPanel
                ) }
            }
        }
    }
    
    // Project operations
    
    fun openProject() {
        viewModelScope.launch {
            // For demonstration, we'll use a fixed path
            val projectPath = "/storage/emulated/0/AndroidStudioProjects/MyProject"
            
            projectManager.openProject(projectPath).onSuccess { projectInfo ->
                _uiState.update { it.copy(
                    projectStructure = ProjectStructure(
                        name = projectInfo.name,
                        path = projectInfo.path,
                        projectType = projectInfo.type.toString(),
                        rootFiles = projectInfo.fileTree
                    )
                ) }
                
                // Save last project path
                settingsManager.saveLastProject(projectPath)
                
                // Log activity
                logActivity("Project opened", mapOf("path" to projectPath))
            }
        }
    }
    
    fun importProject(path: String) {
        viewModelScope.launch {
            projectManager.openProject(path).onSuccess { projectInfo ->
                _uiState.update { it.copy(
                    projectStructure = ProjectStructure(
                        name = projectInfo.name,
                        path = projectInfo.path,
                        projectType = projectInfo.type.toString(),
                        rootFiles = projectInfo.fileTree
                    )
                ) }
                
                // Save last project path
                settingsManager.saveLastProject(path)
                
                // Log activity
                logActivity("Project imported", mapOf("path" to path))
            }
        }
    }
    
    fun closeProject() {
        _uiState.update { it.copy(
            projectStructure = null,
            openFiles = emptyList(),
            activeFile = null
        ) }
        
        // Log activity
        logActivity("Project closed")
    }
    
    fun createProjectFromTemplate(template: ProjectTemplateData) {
        viewModelScope.launch {
            // For demonstration, we'll use a fixed path
            val projectPath = "/storage/emulated/0/AndroidStudioProjects"
            val projectName = template.name.replace(" ", "")
            val packageName = "com.example.${projectName.lowercase()}"
            
            val projectType = when (template.id) {
                "android_basic" -> ProjectType.ANDROID_APP
                "empty_activity" -> ProjectType.ANDROID_APP
                "compose_app" -> ProjectType.COMPOSE_APP
                "mvvm_app" -> ProjectType.MVVM_APP
                "rest_api" -> ProjectType.REST_API_CLIENT
                "game_2d" -> ProjectType.GAME_2D
                "rust_android_lib" -> ProjectType.RUST_ANDROID_LIB
                else -> ProjectType.ANDROID_APP
            }
            
            projectManager.createProject(projectPath, projectName, projectType, packageName).onSuccess { projectInfo ->
                _uiState.update { it.copy(
                    projectStructure = ProjectStructure(
                        name = projectInfo.name,
                        path = projectInfo.path,
                        projectType = projectInfo.type.toString(),
                        rootFiles = projectInfo.fileTree
                    )
                ) }
                
                // Save last project path
                settingsManager.saveLastProject("$projectPath/$projectName")
                
                // Log activity
                logActivity("Project created", mapOf(
                    "template" to template.name,
                    "path" to "$projectPath/$projectName"
                ))
            }
        }
    }
    
    // File operations
    
    fun openFile(filePath: String) {
        viewModelScope.launch {
            fileManager.readFile(filePath).onSuccess { content ->
                val fileName = File(filePath).name
                val language = fileManager.getLanguageFromExtension(File(filePath).extension)
                
                val file = EditorFile(
                    path = filePath,
                    name = fileName,
                    content = content,
                    language = language,
                    isModified = false,
                    lineCount = content.lines().size
                )
                
                // Check if file is already open
                val openFiles = _uiState.value.openFiles.toMutableList()
                val existingIndex = openFiles.indexOfFirst { it.path == filePath }
                
                if (existingIndex >= 0) {
                    // Update existing file
                    openFiles[existingIndex] = file
                } else {
                    // Add new file
                    openFiles.add(file)
                }
                
                _uiState.update { it.copy(
                    openFiles = openFiles,
                    activeFile = file
                ) }
                
                // Highlight syntax
                highlightSyntax(file)
                
                // Log activity
                logActivity("File opened", mapOf("path" to filePath))
            }
        }
    }
    
    fun createNewFile(fileName: String, content: String = "") {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            val filePath = "$projectPath/$fileName"
            
            fileManager.createFile(filePath, content).onSuccess {
                openFile(filePath)
                
                // Log activity
                logActivity("File created", mapOf("path" to filePath))
            }
        }
    }
    
    fun saveFile() {
        viewModelScope.launch {
            val activeFile = _uiState.value.activeFile ?: return@launch
            
            fileManager.writeFile(activeFile.path, activeFile.content).onSuccess {
                // Update file status
                val openFiles = _uiState.value.openFiles.toMutableList()
                val index = openFiles.indexOfFirst { it.path == activeFile.path }
                
                if (index >= 0) {
                    openFiles[index] = activeFile.copy(isModified = false)
                    
                    _uiState.update { it.copy(
                        openFiles = openFiles,
                        activeFile = activeFile.copy(isModified = false)
                    ) }
                    
                    // Log activity
                    logActivity("File saved", mapOf("path" to activeFile.path))
                }
            }
        }
    }
    
    fun updateFileContent(filePath: String, content: String) {
        val openFiles = _uiState.value.openFiles.toMutableList()
        val index = openFiles.indexOfFirst { it.path == filePath }
        
        if (index >= 0) {
            val file = openFiles[index]
            val updatedFile = file.copy(
                content = content,
                isModified = true,
                lineCount = content.lines().size
            )
            
            openFiles[index] = updatedFile
            
            _uiState.update { it.copy(
                openFiles = openFiles,
                activeFile = if (it.activeFile?.path == filePath) updatedFile else it.activeFile
            ) }
            
            // Highlight syntax
            highlightSyntax(updatedFile)
            
            // Auto-save if enabled
            if (_uiState.value.settings.autoSave) {
                viewModelScope.launch {
                    fileManager.writeFile(filePath, content)
                }
            }
        }
    }
    
    fun selectFile(filePath: String) {
        val openFiles = _uiState.value.openFiles
        val file = openFiles.find { it.path == filePath }
        
        if (file != null) {
            _uiState.update { it.copy(activeFile = file) }
            
            // Highlight syntax
            highlightSyntax(file)
        }
    }
    
    fun closeFile(filePath: String) {
        val openFiles = _uiState.value.openFiles.toMutableList()
        val index = openFiles.indexOfFirst { it.path == filePath }
        
        if (index >= 0) {
            val file = openFiles[index]
            
            // Auto-save if modified
            if (file.isModified && _uiState.value.settings.autoSaveOnExit) {
                viewModelScope.launch {
                    fileManager.writeFile(file.path, file.content)
                }
            }
            
            openFiles.removeAt(index)
            
            _uiState.update { it.copy(
                openFiles = openFiles,
                activeFile = if (it.activeFile?.path == filePath) {
                    openFiles.lastOrNull()
                } else {
                    it.activeFile
                }
            ) }
        }
    }
    
    fun autoSaveOnExit() {
        viewModelScope.launch {
            val openFiles = _uiState.value.openFiles
            
            for (file in openFiles) {
                if (file.isModified) {
                    fileManager.writeFile(file.path, file.content)
                }
            }
        }
    }
    
    // Editor operations
    
    fun changeFileLanguage(filePath: String, language: String) {
        val openFiles = _uiState.value.openFiles.toMutableList()
        val index = openFiles.indexOfFirst { it.path == filePath }
        
        if (index >= 0) {
            val file = openFiles[index]
            val updatedFile = file.copy(language = language)
            
            openFiles[index] = updatedFile
            
            _uiState.update { it.copy(
                openFiles = openFiles,
                activeFile = if (it.activeFile?.path == filePath) updatedFile else it.activeFile
            ) }
            
            // Highlight syntax
            highlightSyntax(updatedFile)
        }
    }
    
    fun setEditorLanguage(language: String) {
        val activeFile = _uiState.value.activeFile ?: return
        changeFileLanguage(activeFile.path, language.lowercase())
    }
    
    fun setEditorTheme(themeId: String) {
        themeManager.setTheme(themeId)
        
        _uiState.update { it.copy(
            currentTheme = themeManager.currentTheme.value
        ) }
    }
    
    fun updateEditorState(filePath: String, scrollVertical: Int, scrollHorizontal: Int, selectionStart: Int, selectionEnd: Int) {
        _uiState.update { it.copy(
            editorScrollPosition = Pair(scrollVertical, scrollHorizontal),
            editorSelectionPosition = Pair(selectionStart, selectionEnd)
        ) }
    }
    
    fun highlightSyntax(file: EditorFile) {
        viewModelScope.launch {
            val highlights = editorManager.highlightCode(file.content, file.language)
            _uiState.update { it.copy(syntaxHighlighting = highlights) }
        }
    }
    
    // Insert code at cursor position
    fun insertCodeAtCursor(filePath: String, code: String) {
        val activeFile = _uiState.value.activeFile ?: return
        if (activeFile.path != filePath) return
        
        val content = activeFile.content
        val selectionStart = _uiState.value.editorSelectionPosition.first
        
        // Insert code at cursor position
        val newContent = content.substring(0, selectionStart) + code + content.substring(selectionStart)
        
        // Update file content
        updateFileContent(filePath, newContent)
        
        // Update cursor position
        val newCursorPosition = selectionStart + code.length
        _uiState.update { it.copy(
            editorSelectionPosition = Pair(newCursorPosition, newCursorPosition)
        ) }
    }
    
    // Build operations
    
    fun buildProject(buildType: String) {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            _uiState.update { it.copy(isBuilding = true) }
            
            buildManager.buildProject(projectPath, buildType).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(output)
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Project built", mapOf("buildType" to buildType))
        }
    }
    
    fun runProject() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            val buildType = _uiState.value.settings.selectedBuildType
            
            // First build the project
            _uiState.update { it.copy(isBuilding = true) }
            
            buildManager.buildProject(projectPath, buildType).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(output)
                _uiState.update { it.copy(buildOutputMessages = messages) }
                
                // If build is successful, install and run the APK
                if (output.type == BuildOutputType.SUCCESS) {
                    // Find APK file
                    val apkFile = output.artifacts.firstOrNull { it.endsWith(".apk") }
                    if (apkFile != null) {
                        // Install APK
                        androidPackageManager.installApk(apkFile).collect { installOutput ->
                            messages.add(BuildOutputMessage(
                                type = BuildOutputType.INFO,
                                message = installOutput.content
                            ))
                            _uiState.update { it.copy(buildOutputMessages = messages) }
                        }
                    }
                }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Project run", mapOf("buildType" to buildType))
        }
    }
    
    fun debugProject() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            val buildType = "debug"
            
            // First build the project
            _uiState.update { it.copy(isBuilding = true) }
            
            buildManager.buildProject(projectPath, buildType).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(output)
                _uiState.update { it.copy(buildOutputMessages = messages) }
                
                // If build is successful, install and debug the APK
                if (output.type == BuildOutputType.SUCCESS) {
                    // Find APK file
                    val apkFile = output.artifacts.firstOrNull { it.endsWith(".apk") }
                    if (apkFile != null) {
                        // Install APK
                        androidPackageManager.installApk(apkFile).collect { installOutput ->
                            messages.add(BuildOutputMessage(
                                type = BuildOutputType.INFO,
                                message = installOutput.content
                            ))
                            _uiState.update { it.copy(buildOutputMessages = messages) }
                        }
                        
                        // Start debug session
                        _uiState.update { it.copy(
                            debugSession = DebugSession(
                                sessionId = UUID.randomUUID().toString(),
                                status = "connected",
                                breakpoints = emptyList()
                            )
                        ) }
                    }
                }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Project debugged", mapOf("buildType" to buildType))
        }
    }
    
    fun executeGradleTask(taskName: String) {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            _uiState.update { it.copy(isBuilding = true) }
            
            buildManager.executeGradleTask(projectPath, taskName).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = output,
                    taskName = taskName
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Gradle task executed", mapOf("task" to taskName))
        }
    }
    
    fun refreshGradleTasks() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            buildManager.getGradleTasks(projectPath).onSuccess { tasks ->
                _uiState.update { it.copy(gradleTasks = tasks) }
            }
        }
    }
    
    fun setBuildType(buildType: String) {
        val settings = _uiState.value.settings.copy(selectedBuildType = buildType)
        updateSettings(settings)
    }
    
    fun setCustomGradleArguments(args: String) {
        val settings = _uiState.value.settings.copy(customGradleArgs = args)
        updateSettings(settings)
    }
    
    // Rust build operations
    
    fun buildRustProject(projectPath: String, buildType: String, release: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustBuildManager.buildRustProject(projectPath, buildType, release).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Rust project built", mapOf(
                "buildType" to buildType,
                "release" to release.toString()
            ))
        }
    }
    
    fun cleanRustProject(projectPath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustBuildManager.cleanRustProject(projectPath).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Rust project cleaned", mapOf("path" to projectPath))
        }
    }
    
    fun testRustProject(projectPath: String, release: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustBuildManager.testRustProject(projectPath, release).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Rust project tested", mapOf(
                "path" to projectPath,
                "release" to release.toString()
            ))
        }
    }
    
    fun addRustDependency(projectPath: String, dependencyName: String, dependencyVersion: String) {
        viewModelScope.launch {
            rustBuildManager.addRustDependency(projectPath, dependencyName, dependencyVersion).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            // Log activity
            logActivity("Rust dependency added", mapOf(
                "dependency" to dependencyName,
                "version" to dependencyVersion
            ))
        }
    }
    
    fun removeRustDependency(projectPath: String, dependencyName: String) {
        viewModelScope.launch {
            rustBuildManager.removeRustDependency(projectPath, dependencyName).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            // Log activity
            logActivity("Rust dependency removed", mapOf("dependency" to dependencyName))
        }
    }
    
    fun createRustProject(name: String, path: String, template: String, isAndroidLib: Boolean) {
        viewModelScope.launch {
            createRustProject(rustBuildManager, name, path, template, isAndroidLib).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            // Open the project
            projectManager.openProject("$path/$name").onSuccess { projectInfo ->
                _uiState.update { it.copy(
                    projectStructure = ProjectStructure(
                        name = projectInfo.name,
                        path = projectInfo.path,
                        projectType = projectInfo.type.toString(),
                        rootFiles = projectInfo.fileTree
                    )
                ) }
                
                // Save last project path
                settingsManager.saveLastProject("$path/$name")
                
                // Log activity
                logActivity("Rust project created", mapOf(
                    "name" to name,
                    "path" to path,
                    "template" to template,
                    "isAndroidLib" to isAndroidLib.toString()
                ))
            }
        }
    }
    
    fun generateRustAndroidBindings(projectPath: String) {
        viewModelScope.launch {
            rustBuildManager.generateRustAndroidBindings(projectPath).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            // Log activity
            logActivity("Rust Android bindings generated", mapOf("path" to projectPath))
        }
    }
    
    fun buildRustForAndroidTarget(projectPath: String, target: String, release: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustBuildManager.buildRustForAndroidTarget(projectPath, target, release).collect { output ->
                // Add build output
                val rustOutput = _uiState.value.rustBuildOutput.toMutableList()
                rustOutput.add(output)
                _uiState.update { it.copy(rustBuildOutput = rustOutput) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Rust built for Android target", mapOf(
                "target" to target,
                "release" to release.toString()
            ))
        }
    }
    
    // Rust native build operations
    
    fun buildProjectWithNative(projectPath: String, buildType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustNativeBuildManager.buildProject(projectPath, buildType).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(output)
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Project built with native build system", mapOf("buildType" to buildType))
        }
    }
    
    fun cleanProjectWithNative(projectPath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustNativeBuildManager.cleanProject(projectPath).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(output)
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Project cleaned with native build system", mapOf("path" to projectPath))
        }
    }
    
    fun testProjectWithNative(projectPath: String, release: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true) }
            
            rustNativeBuildManager.testProject(projectPath, release).collect { output ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(output)
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            _uiState.update { it.copy(isBuilding = false) }
            
            // Log activity
            logActivity("Project tested with native build system", mapOf(
                "path" to projectPath,
                "release" to release.toString()
            ))
        }
    }
    
    // Gradle build operations
    
    fun optimizeBuildFile(filePath: String) {
        viewModelScope.launch {
            gradleBuildManager.optimizeBuildFile(filePath).collect { message ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = message
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            // Log activity
            logActivity("Gradle build file optimized", mapOf("path" to filePath))
        }
    }
    
    fun updateDependencies(filePath: String) {
        viewModelScope.launch {
            gradleBuildManager.updateDependencies(filePath).collect { message ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = message
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            // Log activity
            logActivity("Gradle dependencies updated", mapOf("path" to filePath))
        }
    }
    
    fun fixCommonIssues(filePath: String) {
        viewModelScope.launch {
            gradleBuildManager.fixCommonIssues(filePath).collect { message ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = message
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            // Log activity
            logActivity("Gradle common issues fixed", mapOf("path" to filePath))
        }
    }
    
    fun addGradleDependency(filePath: String, dependency: String, configuration: String) {
        viewModelScope.launch {
            gradleFileModifier.addDependency(filePath, dependency, configuration).collect { message ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = message
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            // Log activity
            logActivity("Gradle dependency added", mapOf(
                "dependency" to dependency,
                "configuration" to configuration
            ))
        }
    }
    
    fun removeGradleDependency(filePath: String, dependency: String) {
        viewModelScope.launch {
            gradleFileModifier.removeDependency(filePath, dependency).collect { message ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = message
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
            }
            
            // Log activity
            logActivity("Gradle dependency removed", mapOf("dependency" to dependency))
        }
    }
    
    fun generateBuildReport(projectPath: String) {
        viewModelScope.launch {
            gradleBuildManager.generateBuildReport(projectPath).onSuccess { report ->
                // Add build output
                val messages = _uiState.value.buildOutputMessages.toMutableList()
                messages.add(BuildOutputMessage(
                    type = BuildOutputType.INFO,
                    message = "Build report generated for ${report.modules.size} modules"
                ))
                _uiState.update { it.copy(buildOutputMessages = messages) }
                
                // Log activity
                logActivity("Gradle build report generated", mapOf(
                    "modules" to report.modules.size.toString(),
                    "dependencies" to report.totalDependencies.toString(),
                    "issues" to report.totalIssues.toString()
                ))
            }
        }
    }
    
    // Terminal operations
    
    fun createTerminalSession() {
        viewModelScope.launch {
            terminalManager.createSession().onSuccess { sessionId ->
                val session = TerminalSession(
                    sessionId = sessionId,
                    workingDirectory = "/",
                    isActive = true
                )
                
                _uiState.update { it.copy(
                    terminalSession = session,
                    terminalOutput = emptyList()
                ) }
                
                // Log activity
                logActivity("Terminal session created", mapOf("sessionId" to sessionId))
            }
        }
    }
    
    fun closeTerminalSession() {
        viewModelScope.launch {
            val session = _uiState.value.terminalSession ?: return@launch
            
            terminalManager.closeSession(session.sessionId).onSuccess {
                _uiState.update { it.copy(
                    terminalSession = null,
                    terminalOutput = emptyList()
                ) }
                
                // Log activity
                logActivity("Terminal session closed", mapOf("sessionId" to session.sessionId))
            }
        }
    }
    
    fun executeTerminalCommand(command: String) {
        viewModelScope.launch {
            val session = _uiState.value.terminalSession ?: return@launch
            
            // Add command to output
            val output = _uiState.value.terminalOutput.toMutableList()
            output.add(TerminalOutput(
                sessionId = session.sessionId,
                outputType = "Command",
                content = command,
                timestamp = System.currentTimeMillis()
            ))
            
            _uiState.update { it.copy(terminalOutput = output) }
            
            // Execute command
            terminalManager.executeCommand(session.sessionId, command).collect { terminalOutput ->
                val updatedOutput = _uiState.value.terminalOutput.toMutableList()
                updatedOutput.add(TerminalOutput(
                    sessionId = session.sessionId,
                    outputType = terminalOutput.type.toString(),
                    content = terminalOutput.content,
                    timestamp = terminalOutput.timestamp
                ))
                
                _uiState.update { it.copy(terminalOutput = updatedOutput) }
            }
            
            // Log activity
            logActivity("Terminal command executed", mapOf("command" to command))
        }
    }
    
    fun stopTerminalCommand() {
        viewModelScope.launch {
            val session = _uiState.value.terminalSession ?: return@launch
            
            terminalManager.stopCommand(session.sessionId).onSuccess {
                // Add message to output
                val output = _uiState.value.terminalOutput.toMutableList()
                output.add(TerminalOutput(
                    sessionId = session.sessionId,
                    outputType = "System",
                    content = "Command stopped",
                    timestamp = System.currentTimeMillis()
                ))
                
                _uiState.update { it.copy(terminalOutput = output) }
                
                // Log activity
                logActivity("Terminal command stopped", mapOf("sessionId" to session.sessionId))
            }
        }
    }
    
    fun saveTerminalOutput(fileName: String) {
        viewModelScope.launch {
            val session = _uiState.value.terminalSession ?: return@launch
            
            terminalManager.saveTerminalOutput(session.sessionId, fileName).onSuccess { filePath ->
                // Add message to output
                val output = _uiState.value.terminalOutput.toMutableList()
                output.add(TerminalOutput(
                    sessionId = session.sessionId,
                    outputType = "System",
                    content = "Output saved to $filePath",
                    timestamp = System.currentTimeMillis()
                ))
                
                _uiState.update { it.copy(terminalOutput = output) }
                
                // Log activity
                logActivity("Terminal output saved", mapOf(
                    "fileName" to fileName,
                    "path" to filePath
                ))
            }
        }
    }
    
    fun shareTerminalOutput() {
        viewModelScope.launch {
            val session = _uiState.value.terminalSession ?: return@launch
            
            // Get terminal output
            val output = _uiState.value.terminalOutput
                .filter { it.outputType != "Clear" }
                .joinToString("\n") { "${it.outputType}: ${it.content}" }
            
            // Add message to output
            val terminalOutput = _uiState.value.terminalOutput.toMutableList()
            terminalOutput.add(TerminalOutput(
                sessionId = session.sessionId,
                outputType = "System",
                content = "Output shared",
                timestamp = System.currentTimeMillis()
            ))
            
            _uiState.update { it.copy(terminalOutput = terminalOutput) }
            
            // Log activity
            logActivity("Terminal output shared", mapOf("sessionId" to session.sessionId))
        }
    }
    
    fun bookmarkCommand(command: String, description: String) {
        viewModelScope.launch {
            val bookmarkedCommand = BookmarkedCommand(
                id = UUID.randomUUID().toString(),
                userId = "current_user",
                command = command,
                description = description,
                tags = emptyList(),
                isFavorite = false,
                useCount = 0,
                lastUsed = null,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString()
            )
            
            bookmarkedCommandRepository.createBookmarkedCommand(bookmarkedCommand).onSuccess {
                // Update bookmarked commands
                val bookmarkedCommands = _uiState.value.bookmarkedCommands.toMutableList()
                bookmarkedCommands.add(bookmarkedCommand)
                _uiState.update { it.copy(bookmarkedCommands = bookmarkedCommands) }
                
                // Log activity
                logActivity("Command bookmarked", mapOf("command" to command))
            }
        }
    }
    
    fun useBookmarkedCommand(command: String) {
        viewModelScope.launch {
            // Find the bookmarked command
            val bookmarkedCommand = _uiState.value.bookmarkedCommands.find { it.command == command }
            
            if (bookmarkedCommand != null) {
                // Increment use count
                bookmarkedCommandRepository.incrementUseCount(bookmarkedCommand.id)
                
                // Execute the command
                executeTerminalCommand(command)
            }
        }
    }
    
    // Git operations
    
    fun gitInit() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.initRepository(projectPath).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        _uiState.update { it.copy(isGitInitialized = true) }
                        
                        // Log activity
                        logActivity("Git repository initialized", mapOf("path" to projectPath))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git initialization failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun getGitStatus() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.getStatus(projectPath).collect { result ->
                when (result) {
                    is GitResult.StatusResult -> {
                        _uiState.update { it.copy(
                            gitStatus = result.status,
                            isGitInitialized = true
                        ) }
                        
                        // Log activity
                        logActivity("Git status retrieved", mapOf(
                            "files" to result.status.size.toString()
                        ))
                    }
                    is GitResult.Error -> {
                        if (result.message.contains("not a git repository")) {
                            _uiState.update { it.copy(isGitInitialized = false) }
                        }
                        
                        // Log error
                        logActivity("Git status failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitAdd(file: String) {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.addFiles(projectPath, listOf(file)).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        // Refresh status
                        getGitStatus()
                        
                        // Log activity
                        logActivity("Git add", mapOf("file" to file))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git add failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitCommit(message: String) {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.commit(projectPath, message).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        // Refresh status
                        getGitStatus()
                        
                        // Log activity
                        logActivity("Git commit", mapOf("message" to message))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git commit failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitPush() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.push(projectPath).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        // Log activity
                        logActivity("Git push", mapOf("result" to "success"))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git push failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitPull() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.pull(projectPath).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        // Refresh status
                        getGitStatus()
                        
                        // Log activity
                        logActivity("Git pull", mapOf("result" to "success"))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git pull failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitCreateBranch(branchName: String) {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.createBranch(projectPath, branchName).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        // Update current branch
                        _uiState.update { it.copy(currentBranch = branchName) }
                        
                        // Refresh branches
                        gitBranches()
                        
                        // Log activity
                        logActivity("Git branch created", mapOf("branch" to branchName))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git branch creation failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitCheckoutBranch(branchName: String) {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.switchBranch(projectPath, branchName).collect { result ->
                when (result) {
                    is GitResult.Success -> {
                        // Update current branch
                        _uiState.update { it.copy(currentBranch = branchName) }
                        
                        // Refresh status
                        getGitStatus()
                        
                        // Log activity
                        logActivity("Git checkout", mapOf("branch" to branchName))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git checkout failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitBranches() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.getBranches(projectPath).collect { result ->
                when (result) {
                    is GitResult.BranchResult -> {
                        _uiState.update { it.copy(gitBranches = result.branches) }
                        
                        // Update current branch
                        val currentBranch = result.branches.find { it.isCurrent }
                        if (currentBranch != null) {
                            _uiState.update { it.copy(currentBranch = currentBranch.name) }
                        }
                        
                        // Log activity
                        logActivity("Git branches retrieved", mapOf(
                            "count" to result.branches.size.toString()
                        ))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git branches failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun gitLog() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectStructure?.path ?: return@launch
            
            gitIntegration.getLog(projectPath).collect { result ->
                when (result) {
                    is GitResult.LogResult -> {
                        _uiState.update { it.copy(gitCommits = result.commits) }
                        
                        // Log activity
                        logActivity("Git log retrieved", mapOf(
                            "count" to result.commits.size.toString()
                        ))
                    }
                    is GitResult.Error -> {
                        // Log error
                        logActivity("Git log failed", mapOf("error" to result.message))
                    }
                    else -> {}
                }
            }
        }
    }
    
    // Settings operations
    
    fun updateSettings(settings: IDESettings) {
        settingsManager.updateSettings(settings)
        
        _uiState.update { it.copy(
            settings = settings,
            isDarkTheme = settings.isDarkTheme,
            showLeftPanel = settings.showProjectExplorer,
            showRightPanel = settings.showToolWindows,
            showBottomPanel = settings.showBottomPanel
        ) }
        
        // Log activity
        logActivity("Settings updated")
    }
    
    fun showSettings() {
        _uiState.update { it.copy(showSettingsScreen = true) }
    }
    
    fun hideSettings() {
        _uiState.update { it.copy(showSettingsScreen = false) }
    }
    
    fun toggleTheme() {
        val settings = _uiState.value.settings.copy(isDarkTheme = !_uiState.value.settings.isDarkTheme)
        updateSettings(settings)
    }
    
    fun toggleLeftPanel() {
        _uiState.update { it.copy(showLeftPanel = !it.showLeftPanel) }
    }
    
    fun toggleRightPanel() {
        _uiState.update { it.copy(showRightPanel = !it.showRightPanel) }
    }
    
    fun toggleBottomPanel() {
        _uiState.update { it.copy(showBottomPanel = !it.showBottomPanel) }
    }
    
    // Plugin operations
    
    fun showPluginManager() {
        _uiState.update { it.copy(showPluginManager = true) }
    }
    
    fun hidePluginManager() {
        _uiState.update { it.copy(showPluginManager = false) }
    }
    
    fun installPlugin(plugin: PluginMetadata) {
        viewModelScope.launch {
            pluginManager.installPlugin(plugin.downloadUrl).collect { progress ->
                // Log progress
                when (progress) {
                    is InstallationProgress.Completed -> {
                        // Refresh plugins
                        refreshPlugins()
                        
                        // Log activity
                        logActivity("Plugin installed", mapOf(
                            "name" to plugin.name,
                            "version" to plugin.version
                        ))
                    }
                    is InstallationProgress.Failed -> {
                        // Log error
                        logActivity("Plugin installation failed", mapOf(
                            "name" to plugin.name,
                            "error" to progress.message
                        ))
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun uninstallPlugin(pluginId: String) {
        viewModelScope.launch {
            pluginManager.uninstallPlugin(pluginId).onSuccess {
                // Refresh plugins
                refreshPlugins()
                
                // Log activity
                logActivity("Plugin uninstalled", mapOf("id" to pluginId))
            }
        }
    }
    
    fun enablePlugin(pluginId: String) {
        viewModelScope.launch {
            pluginManager.enablePlugin(pluginId).onSuccess {
                // Refresh plugins
                refreshPlugins()
                
                // Log activity
                logActivity("Plugin enabled", mapOf("id" to pluginId))
            }
        }
    }
    
    fun disablePlugin(pluginId: String) {
        viewModelScope.launch {
            pluginManager.disablePlugin(pluginId).onSuccess {
                // Refresh plugins
                refreshPlugins()
                
                // Log activity
                logActivity("Plugin disabled", mapOf("id" to pluginId))
            }
        }
    }
    
    fun refreshPlugins() {
        pluginManager.refreshPlugins()
        
        viewModelScope.launch {
            _uiState.update { it.copy(
                installedPlugins = pluginManager.installedPlugins.value,
                availablePlugins = pluginManager.availablePlugins.value
            ) }
        }
    }
    
    // AI Assistant operations
    
    fun showAiAssistant() {
        _uiState.update { it.copy(showAiAssistant = true) }
    }
    
    fun hideAiAssistant() {
        _uiState.update { it.copy(showAiAssistant = false) }
    }
    
    fun aiExplainCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            
            geminiApiService.explainCode(code, _uiState.value.activeFile?.language ?: "kotlin").collect { response ->
                when (response) {
                    is GeminiResponse.Success -> {
                        // Show AI assistant with explanation
                        _uiState.update { it.copy(
                            showAiAssistant = true,
                            isAnalyzing = false
                        ) }
                    }
                    is GeminiResponse.Error -> {
                        _uiState.update { it.copy(isAnalyzing = false) }
                        
                        // Log error
                        logActivity("AI code explanation failed", mapOf("error" to response.message))
                    }
                    is GeminiResponse.Loading -> {
                        // Already showing loading indicator
                    }
                }
            }
        }
    }
    
    fun aiFixCode(code: String, error: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            
            geminiApiService.fixCodeIssues(code, error, _uiState.value.activeFile?.language ?: "kotlin").collect { response ->
                when (response) {
                    is GeminiResponse.Success -> {
                        // Show AI assistant with fix
                        _uiState.update { it.copy(
                            showAiAssistant = true,
                            isAnalyzing = false
                        ) }
                    }
                    is GeminiResponse.Error -> {
                        _uiState.update { it.copy(isAnalyzing = false) }
                        
                        // Log error
                        logActivity("AI code fix failed", mapOf("error" to response.message))
                    }
                    is GeminiResponse.Loading -> {
                        // Already showing loading indicator
                    }
                }
            }
        }
    }
    
    fun aiFixError(error: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            
            val activeFile = _uiState.value.activeFile
            if (activeFile != null) {
                geminiApiService.fixCodeIssues(activeFile.content, error, activeFile.language).collect { response ->
                    when (response) {
                        is GeminiResponse.Success -> {
                            // Show AI assistant with fix
                            _uiState.update { it.copy(
                                showAiAssistant = true,
                                isAnalyzing = false
                            ) }
                        }
                        is GeminiResponse.Error -> {
                            _uiState.update { it.copy(isAnalyzing = false) }
                            
                            // Log error
                            logActivity("AI error fix failed", mapOf("error" to response.message))
                        }
                        is GeminiResponse.Loading -> {
                            // Already showing loading indicator
                        }
                    }
                }
            } else {
                _uiState.update { it.copy(isAnalyzing = false) }
            }
        }
    }
    
    fun generateCode(prompt: String, language: String): Flow<GeminiResponse> {
        return geminiApiService.generateCode(prompt, language)
    }
    
    // Build system operations
    
    fun setBuildSystem(buildSystem: BuildSystemType) {
        _uiState.update { it.copy(selectedBuildSystem = buildSystem) }
        
        // Log activity
        logActivity("Build system changed", mapOf("buildSystem" to buildSystem.toString()))
    }
    
    // Root operations
    
    fun requestRootAccess() {
        viewModelScope.launch {
            isRootAccessRequested = true
            
            rootManager.requestRootAccess().onSuccess { hasRoot ->
                _uiState.update { it.copy(
                    settings = it.settings.copy(enableRootFeatures = hasRoot)
                ) }
                
                // Log activity
                logActivity("Root access requested", mapOf("granted" to hasRoot.toString()))
            }
        }
    }
    
    // Logging
    
    fun logActivity(action: String, details: Map<String, String> = emptyMap()) {
        Log.d("EnhancedMainViewModel", "Activity: $action, Details: $details")
    }
    
    // UI state
    
    data class UiState(
        // Project state
        val projectStructure: ProjectStructure? = null,
        val openFiles: List<EditorFile> = emptyList(),
        val activeFile: EditorFile? = null,
        
        // Editor state
        val syntaxHighlighting: List<SyntaxHighlight> = emptyList(),
        val editorScrollPosition: Pair<Int, Int> = Pair(0, 0),
        val editorSelectionPosition: Pair<Int, Int> = Pair(0, 0),
        
        // Build state
        val isBuilding: Boolean = false,
        val isAnalyzing: Boolean = false,
        val buildOutput: BuildOutput? = null,
        val buildOutputMessages: List<BuildOutputMessage> = emptyList(),
        val gradleTasks: List<GradleTask> = emptyList(),
        val problems: List<Problem> = emptyList(),
        
        // Debug state
        val debugSession: DebugSession? = null,
        
        // Terminal state
        val terminalSession: TerminalSession? = null,
        val terminalOutput: List<TerminalOutput> = emptyList(),
        val bookmarkedCommands: List<BookmarkedCommand> = emptyList(),
        
        // Git state
        val isGitInitialized: Boolean = false,
        val gitStatus: List<GitFileStatus> = emptyList(),
        val gitBranches: List<GitBranch> = emptyList(),
        val gitCommits: List<GitCommit> = emptyList(),
        val currentBranch: String = "main",
        
        // UI state
        val isDarkTheme: Boolean = true,
        val showLeftPanel: Boolean = true,
        val showRightPanel: Boolean = true,
        val showBottomPanel: Boolean = true,
        val showSettingsScreen: Boolean = false,
        val showPluginManager: Boolean = false,
        val showAiAssistant: Boolean = false,
        
        // Settings
        val settings: IDESettings = IDESettings(),
        
        // Themes
        val availableThemes: List<EditorTheme> = emptyList(),
        val currentTheme: EditorTheme = EditorTheme(
            id = "dark_default",
            name = "Dark Default",
            description = "Default dark theme",
            isCustom = false,
            colors = EditorColors(
                background = "#1E1E1E",
                foreground = "#D4D4D4",
                selection = "#264F78",
                lineNumber = "#858585",
                currentLine = "#2A2D2E",
                cursor = "#FFFFFF",
                keyword = "#569CD6",
                string = "#CE9178",
                comment = "#6A9955",
                number = "#B5CEA8",
                function = "#DCDCAA",
                type = "#4EC9B0",
                variable = "#9CDCFE",
                operator = "#D4D4D4",
                bracket = "#FFD700",
                error = "#F44747",
                warning = "#FF8C00",
                info = "#3794FF"
            )
        ),
        
        // Languages
        val availableLanguages: List<LanguageConfig> = emptyList(),
        
        // Plugins
        val installedPlugins: List<Plugin> = emptyList(),
        val availablePlugins: List<PluginMetadata> = emptyList(),
        
        // Rust
        val rustBuildOutput: List<RustBuildOutput> = emptyList(),
        val rustCrateInfo: RustCrateInfo? = null,
        
        // Build system
        val selectedBuildSystem: BuildSystemType = BuildSystemType.GRADLE,
        val isNativeBuildSystemAvailable: Boolean = false,
        val rustVersion: String = "Unknown",
        val nativeBuildSystemStatus: Map<String, Any> = emptyMap()
    )
}