package com.anyoneide.app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anyoneide.app.data.DataModule
import com.anyoneide.app.data.room.AppDatabase

class EnhancedMainViewModelFactory(
    private val application: Application,
    private val database: AppDatabase
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnhancedMainViewModel::class.java)) {
            val userRepository = DataModule.provideUserRepository(application.applicationContext)
            val projectRepository = DataModule.provideProjectRepository(application.applicationContext)
            val pluginRepository = DataModule.providePluginRepository(application.applicationContext)
            val terminalRepository = DataModule.provideTerminalRepository(application.applicationContext)
            val themeRepository = DataModule.provideThemeRepository(application.applicationContext)
            val templateRepository = DataModule.provideTemplateRepository(application.applicationContext)
            val codeSnippetRepository = DataModule.provideCodeSnippetRepository(application.applicationContext)
            val activityLogRepository = DataModule.provideActivityLogRepository(application.applicationContext)
            val storageRepository = DataModule.provideStorageRepository(application.applicationContext)
            val bookmarkedCommandRepository = DataModule.provideBookmarkedCommandRepository(application.applicationContext)

            return EnhancedMainViewModel(
                application,
                userRepository,
                projectRepository,
                pluginRepository,
                terminalRepository,
                themeRepository,
                templateRepository,
                codeSnippetRepository,
                activityLogRepository,
                storageRepository,
                bookmarkedCommandRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}