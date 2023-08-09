package io.github.sunshinewzy.shining.core.addon

import io.github.sunshinewzy.shining.api.addon.ShiningAddon
import io.github.sunshinewzy.shining.core.addon.loader.LibraryLoaderPools
import io.github.sunshinewzy.shining.core.addon.loader.ShiningAddonInitializingException
import io.github.sunshinewzy.shining.core.addon.loader.ShiningAddonJarLoader
import io.github.sunshinewzy.shining.core.addon.loader.ShiningAddonLoadingException
import io.github.sunshinewzy.shining.utils.CollectionUtils
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import java.io.File
import java.util.logging.Level
import java.util.regex.Pattern

@SkipTo(LifeCycle.ENABLE)
object ShiningAddonRegistry {
    
    val addonsFolder: File = File(getDataFolder(), "addons/")
    internal val jarLoaders: MutableMap<String, ShiningAddonJarLoader> = HashMap()
    internal val addons: MutableMap<String, ShiningAddon> = HashMap()

    private val INVALID_NAMES = hashSetOf("shining", "minecraft", "itemsadder", "oraxen", "mmoitems")
    private val VALID_NAME = Pattern.compile("[a-zA-Z0-9_-]+")
    
    
    init {
        addonsFolder.mkdirs()
    }
    
    
    fun hasAddon(name: String): Boolean = name in addons
    
    fun getAddon(name: String): ShiningAddon? = addons[name]
    
    
    internal fun loadAddons() {
        info("Loading addons...")
        loadJarAddons()
        
        info("Initializing addons...")
        initJarAddons()
        
        info("Enabling addons...")
        enableJarAddons()
    }
    
    internal fun loadJarAddons() {
        addonsFolder.listFiles()?.forEach { file -> 
            if (file.isFile && file.extension == "jar") {
                try {
                    val loader = ShiningAddonJarLoader(file)
                    val description = loader.description
                    val name = description.name
                    
                    if (!VALID_NAME.matcher(name).matches() || name.lowercase() in INVALID_NAMES) {
                        loader.logger.severe("Failed to be loaded: '$name' is not a valid name")
                        return@forEach
                    }
                    
                    if (name in jarLoaders) {
                        throw ShiningAddonLoadingException(name, "Duplicate addon name '$name' for ${loader.file} and ${jarLoaders[name]!!.file}")
                    }
                    
                    loader.logger.info("Loaded ${description.getNameAndVersion()}")
                    jarLoaders[name] = loader
                } catch (ex: ShiningAddonLoadingException) {
                    throw ex
                } catch (th: Throwable) {
                    throw ShiningAddonLoadingException(file.nameWithoutExtension, th)
                }
            }
        }
        
        jarLoaders.values.forEach { loader ->
            val description = loader.description
            
            val missingDependencies = description.depend.filter { it !in jarLoaders.keys }
            if (missingDependencies.isNotEmpty()) {
                throw ShiningAddonLoadingException(description.name, "Missing addon(s): " +
                        missingDependencies.joinToString { "[$it]" })
            }
        }
    }
    
    internal fun initJarAddons() {
        LibraryLoaderPools.init(jarLoaders.values)
        val addonLoaders = CollectionUtils.sortDependencies(jarLoaders.values) {
            (it.description.depend + it.description.softdepend).mapNotNullTo(HashSet(), jarLoaders::get)
        }
        addonLoaders.forEach { loader ->
            val description = loader.description
            loader.logger.info("Initializing ${description.getNameAndVersion()}")
            
            try {
                loader.classLoader.setDependencyClassLoaders()
                val addon = loader.load()
                addons[addon.description.name] = addon
                addon.onInit()
            } catch (th: Throwable) {
                throw ShiningAddonInitializingException(loader, th)
            }
        }
    }
    
    internal fun enableJarAddons() {
        addons.values.forEach { addon ->
            addon.logger.info("Enabling ${addon.description.getNameAndVersion()}")

            try {
                addon.onEnable()
            } catch (th: Throwable) {
                addon.logger.log(Level.SEVERE, "Failed to enable ${addon.description.getNameAndVersion()}", th)
            }
        }
    }

    internal fun activeJarAddons() {
        addons.values.forEach { addon ->
            addon.logger.info("Activating ${addon.description.getNameAndVersion()}")

            try {
                addon.onActive()
            } catch (th: Throwable) {
                addon.logger.log(Level.SEVERE, "Failed to activate ${addon.description.getNameAndVersion()}", th)
            }
        }
    }

    internal fun disableJarAddons() {
        addons.values.forEach { addon ->
            addon.logger.info("Disabling ${addon.description.getNameAndVersion()}")

            try {
                addon.onDisable()
                addon.addonManager.unregisterListeners()
            } catch (th: Throwable) {
                addon.logger.log(Level.SEVERE, "Failed to disable ${addon.description.getNameAndVersion()}", th)
            }
        }
    }
    
}