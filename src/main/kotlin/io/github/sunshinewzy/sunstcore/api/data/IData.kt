package io.github.sunshinewzy.sunstcore.api.data

import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import org.bukkit.configuration.ConfigurationSection

/**
 * Represent a [Map] like object, capable of storing custom data in it.
 * 
 * 
 * 表示一个类似 [Map] 的对象，可以存储自定义数据。
 */
interface IData {
    
    val name: String
    
    val container: IDataContainer
    
    val parent: IData?


    /**
     * Set the specified path to the given value.
     *
     *
     * Any existing entry will be replaced, regardless of
     * what the new value is.
     *
     * @param path Path of the object to set.
     * @param value New value to set the path to.
     */
    operator fun set(path: String, value: Any)

    /**
     * Get the requested Object by path.
     *
     *
     * If the Object does not exist, this will return null.
     *
     * @param path Path of the Object to get.
     * @return Requested Object.
     */
    operator fun get(path: String): Any?

    /**
     * Get the requested Object by path, returning a default value if not
     * found.
     *
     * @param path Path of the Object to get.
     * @param default  The default value to return if the path is not found.
     * @return Requested Object.
     */
    operator fun get(path: String, default: Any): Any

    /**
     * Get the requested Object with reified type [T] by path.
     *
     *
     * If the Object does not exist, this will return null.
     *
     * @param path Path of the Object to get.
     * @param type The class of reified type [T].
     * @return Requested Object with reified type [T].
     */
    fun <T> getWithType(path: String, type: Class<T>): T?

    /**
     * Get the requested Object with reified type [T] by path, returning
     * a default value if not found.
     *
     * @param path Path of the Object to get.
     * @param type The class of reified type [T].
     * @param default  The default value to return if the path is not found.
     * @return Requested Object with reified type [T].
     */
    fun <T> getWithType(path: String, type: Class<T>, default: T): T

    /**
     * Get the requested [IData] by path.
     * 
     * 
     * If the [IData] does not exist, this will return null.
     *
     * @param path Path of the [IData] to get.
     * @return Requested [IData].
     */
    fun getData(path: String): IData?

    /**
     * Create an empty [IData] at the specified path.
     * 
     * 
     * Any value that was previously set at this path will be overwritten. If
     * the previous value was itself a [IData], it will
     * be orphaned.
     *
     * @param path Path to create the [IData] at.
     * @return Newly created [IData]
     */
    fun createData(path: String): IData

    /**
     * Remove the requested Object by path.
     * 
     * @param path Path of the Object to remove.
     */
    fun remove(path: String)

    /**
     * Get a set containing all keys in this [IData].
     *
     *
     * If deep is set to true, then this will contain all the keys
     * within any child [IData]s (and their children, etc.).
     * These will be in a valid path notation for you to use.
     *
     *
     * If deep is set to false, then this will contain only the keys of any
     * direct children, and not their own children.
     *
     * @param deep Whether to get a deep list, as opposed to a shallow list.
     * @return Set of keys contained within this [IData].
     */
    fun getKeys(deep: Boolean): Set<String>

    /**
     * Gets a Map containing all keys and their values for this section.
     *
     *
     * If deep is set to true, then this will contain all the keys and values
     * within any child [ConfigurationSection]s (and their children,
     * etc). These keys will be in a valid path notation for you to use.
     *
     *
     * If deep is set to false, then this will contain only the keys and
     * values of any direct children, and not their own children.
     *
     * @param deep Whether to get a deep list, as opposed to a shallow
     * list.
     * @return Map of keys and values of this section.
     */
    fun getValues(deep: Boolean): Map<String?, Any?>

    /**
     * Checks if this [ConfigurationSection] contains the given path.
     *
     *
     * If the value for the requested path does not exist but a default value
     * has been specified, this will return true.
     *
     * @param path Path to check for existence.
     * @return True if this section contains the requested path, either via
     * default or being set.
     * @throws IllegalArgumentException Thrown when path is null.
     */
    operator fun contains(path: String): Boolean

    /**
     * Checks if this [ConfigurationSection] contains the given path.
     *
     *
     * If the value for the requested path does not exist, the boolean parameter
     * of true has been specified, a default value for the path exists, this
     * will return true.
     *
     *
     * If a boolean parameter of false has been specified, true will only be
     * returned if there is a set value for the specified path.
     *
     * @param path Path to check for existence.
     * @param ignoreDefault Whether to ignore if a default value for the
     * specified path exists.
     * @return True if this section contains the requested path, or if a default
     * value exist and the boolean parameter for this method is true.
     * @throws IllegalArgumentException Thrown when path is null.
     */
    fun contains(path: String, ignoreDefault: Boolean): Boolean
    
    
    companion object {
        /**
         * Get the requested Object with reified type [T] by path.
         *
         *
         * If the Object does not exist, this will return null.
         *
         * @param path Path of the Object to get.
         * @return Requested Object with reified type [T].
         */
        inline fun <reified T> IData.getWithType(path: String): T? {
            return getWithType(path, T::class.java)
        }

        /**
         * Get the requested Object with reified type [T] by path, returning
         * a default value if not found.
         *
         * @param path Path of the Object to get.
         * @param default  The default value to return if the path is not found.
         * @return Requested Object with reified type [T].
         */
        inline fun <reified T> IData.getWithType(path: String, default: T): T {
            return getWithType(path, T::class.java, default)
        }

        
        /**
         * Create a relative path to the given [IData] from
         * the given relative [IData].
         * 
         * @param data [IData] to create a path for.
         * @param key Name of the specified [IData].
         * @param relativeTo [IData] to create the path relative to.
         * @return Full path of the section from its root.
         * Empty when the containers of [data] and [relativeTo] are different.
         */
        @JvmStatic
        fun createPath(data: IData, key: String, relativeTo: IData): String {
            val container = data.container
            if(container !== relativeTo.container)
                return ""
            
            val separator = container.options.pathSeparator
            
            val builder = StringBuilder()
            var parent: IData? = data
            while(parent != null && parent !== relativeTo) {
                if(builder.isNotEmpty())
                    builder.insert(0, separator)
                
                builder.insert(0, parent.name)
                parent = parent.parent
            }
            
            if(key.isNotEmpty()) {
                if(builder.isNotEmpty())
                    builder.append(separator)
                
                builder.append(key)
            }

            return builder.toString()
        }
    }
    
}