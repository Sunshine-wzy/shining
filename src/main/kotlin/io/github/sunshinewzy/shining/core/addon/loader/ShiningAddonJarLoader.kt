package io.github.sunshinewzy.shining.core.addon.loader

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.addon.*
import java.io.File
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmName

class ShiningAddonJarLoader(val file: File) {
    
    val classLoader: ShiningAddonClassLoader = ShiningAddonClassLoader(this, javaClass.classLoader)
    val description: ShiningAddonJarDescription
    val logger: ShiningAddonLogger
    lateinit var addon: ShiningAddon
        private set
    
    
    init {
        val descriptionFile = classLoader.getResourceAsStream("addon.yml")
            ?: throw ShiningAddonLoadingException(file.nameWithoutExtension, "Could not find addon.yml")
        
        description = Shining.yamlObjectMapper.readValue(descriptionFile)
        logger = ShiningAddonLogger(description.name)
    }
    
    
    fun load(): ShiningAddon {
        val mainClass = classLoader.loadClass(description.main).kotlin
        val instance = mainClass.objectInstance
            ?: kotlin.runCatching { mainClass.createInstance() }.getOrNull()
            ?: throw ShiningAddonLoadingException(description.name, "Main class is not a singleton object and has no default constructor")
        addon = instance as? ShiningAddon
            ?: throw ShiningAddonLoadingException(description.name, "Main class is not a subclass of ${ShiningAddon::class.jvmName}")
        
        addon.logger = logger
        addon.file = file
        addon.dataFolder = File(ShiningAddonRegistry.addonsFolder, description.name)
        addon.description = description
        addon.addonManager = ShiningAddonManager(addon)
        return addon
    }
    
}