package io.github.sunshinewzy.shining.core.addon.loader

import io.github.sunshinewzy.shining.core.addon.ShiningAddonManager
import java.net.URLClassLoader

class ShiningAddonClassLoader(
    val loader: ShiningAddonJarLoader,
    parent: ClassLoader
) : URLClassLoader(arrayOf(loader.file.toURI().toURL()), parent) {
    
    private var libraryLoader: URLClassLoader? = null
    private var addonDependencies: List<ShiningAddonClassLoader>? = null
    
    
    fun setDependencyClassLoaders() {
        libraryLoader = LibraryLoaderPools[loader]
        addonDependencies = (loader.description.depend + loader.description.softdepend)
            .mapNotNull { ShiningAddonManager.jarLoaders[it]?.classLoader }
            .takeUnless(List<*>::isEmpty)
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            // Check if class is already loaded
            var c: Class<*>? = findLoadedClass(name)

            // Load class from this addon
            if (c == null) {
                c = loadAddonClass(name)
            }

            // Load class from libraries (includes dependency libraries)
            if (c == null) {
                c = loadLibraryClass(name)
            }

            // Load class from addon dependencies
            if (c == null) {
                c = addonDependencies?.firstNotNullOfOrNull { runCatching { it.loadClass(name, true) }.getOrNull() }
            }

            // Load class from parent (shining classloader)
            if (c == null) {
                c = parent.loadClass(name)
            }

            // Should never be true because parent class loader should throw ClassNotFoundException before
            if (c == null) {
                throw ClassNotFoundException(name)
            }

            if (resolve) {
                resolveClass(c)
            }

            return c
        }
    }

    private fun loadAddonClass(name: String): Class<*>? {
        return runCatching { findClass(name) }.getOrNull()
    }

    private fun loadLibraryClass(name: String): Class<*>? {
        libraryLoader?.let {
            return it.runCatching { this.loadClass(name) }.getOrNull()
        }

        return null
    }
    
}