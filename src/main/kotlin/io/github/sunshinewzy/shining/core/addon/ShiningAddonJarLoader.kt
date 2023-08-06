package io.github.sunshinewzy.shining.core.addon

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.sunshinewzy.shining.Shining
import java.io.File

class ShiningAddonJarLoader(val file: File) {
    
    val classLoader: ShiningAddonClassLoader = ShiningAddonClassLoader(this, javaClass.classLoader)
    val description: ShiningAddonJarDescription
    
    
    init {
        val descriptionFile = classLoader.getResourceAsStream("addon.yml")
            ?: throw ShiningAddonLoadingException(file.nameWithoutExtension, "Could not find addon.yml")
        
        description = Shining.yamlObjectMapper.readValue(descriptionFile)
        
    }
    
}