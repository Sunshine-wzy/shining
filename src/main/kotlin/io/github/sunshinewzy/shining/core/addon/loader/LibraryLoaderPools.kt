package io.github.sunshinewzy.shining.core.addon.loader

import io.github.sunshinewzy.shining.api.ShiningConfig
import io.github.sunshinewzy.shining.utils.poll
import taboolib.common.PrimitiveIO
import taboolib.common.env.Dependency
import taboolib.common.env.DependencyDownloader
import taboolib.common.env.Repository
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger


object LibraryLoaderPools {

    const val defaultLibrary = "libs"
    
    private val jarLoaderMap: MutableMap<ShiningAddonJarLoader, URLClassLoader?> = HashMap()
    
    
    fun init(jarLoaders: Collection<ShiningAddonJarLoader>) {
        val pools = ArrayList<ArrayList<ShiningAddonJarLoader>>()

        val remaining = jarLoaders.toMutableList()
        while (remaining.isNotEmpty()) {
            pools += arrayListOf(remaining.poll()!!)

            remaining.removeIf { currentLoader ->
                pools.forEach { pool ->
                    if (pool.any { currentLoader.dependsOn(it) || it.dependsOn(currentLoader) }) {
                        pool += currentLoader
                        return@removeIf true
                    }
                }

                return@removeIf false
            }
        }

        val nullLoaders = ConcurrentHashMap.newKeySet<ShiningAddonJarLoader>()
        val loadersMap = ConcurrentHashMap<ShiningAddonJarLoader, URLClassLoader>()
        pools.parallelStream().forEach { pool ->
            val libraryClassLoader = createPooledLibraryClassLoader(pool)
            if (libraryClassLoader == null) {
                nullLoaders += pool
            } else {
                pool.forEach {
                    loadersMap[it] = libraryClassLoader
                }
            }
        }

        jarLoaderMap.clear()
        nullLoaders.forEach {
            jarLoaderMap[it] = null
        }
        jarLoaderMap += loadersMap
    }

    operator fun get(jarLoader: ShiningAddonJarLoader): URLClassLoader? = jarLoaderMap[jarLoader]

    
    private fun ShiningAddonJarLoader.dependsOn(other: ShiningAddonJarLoader): Boolean {
        val name = other.description.name
        return name in description.depend || name in description.softdepend
    }

    private fun createPooledLibraryClassLoader(loaders: List<ShiningAddonJarLoader>): URLClassLoader? {
        if (loaders.all { it.description.libraries.isEmpty() })
            return null

        val baseDir = File(defaultLibrary)
        val urls = ConcurrentHashMap.newKeySet<URL>()
        loaders.parallelStream().forEach { loader ->
            urls += loadLibrary(baseDir, loader.description.libraries, loader.description.repositories, loader.logger)
        }

        return URLClassLoader(urls.toTypedArray(), null)
    }
    
    
    fun loadLibrary(baseDir: File, libraries: List<Dependency>, repositories: List<String>, logger: Logger): Set<URL> {
        val downloader = DependencyDownloader(baseDir)
        repositories.forEach {
            downloader.addRepository(Repository(it))
        }
        if (downloader.repositories.isEmpty()) {
            downloader.addRepository(Repository(ShiningConfig.defaultRepositoryCentral))
        }

        val urls = HashSet<URL>()
        libraries.forEach { dependency ->
            // Resolve dependency
            val pomFile = File(
                baseDir,
                String.format(
                    "%s/%s/%s/%s-%s.pom",
                    dependency.groupId.replace('.', '/'),
                    dependency.artifactId,
                    dependency.version,
                    dependency.artifactId,
                    dependency.version
                )
            )
            val pomFile1 = File(pomFile.path + ".sha1")
            
            // Verify file integrity
            if (PrimitiveIO.validation(pomFile, pomFile1)) {
                downloader.loadDependencyFromInputStream(pomFile.toPath().toUri().toURL().openStream())
            } else {
                pomFile.parentFile.mkdirs()
                logger.info(
                    String.format(
                        "Downloading library %s:%s:%s",
                        dependency.groupId,
                        dependency.artifactId,
                        dependency.version
                    )
                )

                var e: IOException? = null
                for (repo in downloader.repositories) {
                    e = try {
                        repo.downloadFile(dependency, pomFile)
                        null
                    } catch (ex: Exception) {
                        IOException(String.format("Unable to find download for %s (%s)", dependency, repo.url), ex)
                    }
                }
                if (e != null) throw e

                try {
                    downloader.loadDependencyFromInputStream(pomFile.toPath().toUri().toURL().openStream())
                } catch (ex: FileNotFoundException) {
                    throw ex
                }
            }
            
            // Load the dependency itself
            urls += getLibraryUrls(baseDir, downloader.loadDependency(downloader.repositories, dependency), logger)
        }
        return urls
    }

    fun getLibraryUrls(baseDir: File, libraries: Set<Dependency>, logger: Logger): Set<URL> {
        val urls = HashSet<URL>()
        for (library in libraries) {
            val file = library.findFile(baseDir, "jar")
            if (file.exists()) {
                logger.info(
                    String.format(
                        "Loading library %s:%s:%s",
                        library.groupId,
                        library.artifactId,
                        library.version
                    )
                )
                urls += file.toURI().toURL()
            }
        }
        return urls
    }

}