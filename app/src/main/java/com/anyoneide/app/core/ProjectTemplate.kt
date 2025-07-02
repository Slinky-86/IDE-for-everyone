package com.anyoneide.app.core

import java.io.File

/**
 * Interface for project templates
 */
interface ProjectTemplate {
    val id: String
    val name: String
    val description: String
    val category: String
    val features: List<String>
    val difficulty: String
    val estimatedTime: String
    
    /**
     * Create a project from this template
     * 
     * @param projectDir The directory where the project should be created
     * @param packageName The package name for the project
     * @return true if the project was created successfully, false otherwise
     */
    fun create(projectDir: File, packageName: String): Boolean
}