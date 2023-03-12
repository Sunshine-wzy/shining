package io.github.sunshinewzy.shining.api.data

/**
 * Represent a [Map] like object, capable of storing custom data in it.
 *
 *
 * 表示一个类似 [Map] 的对象，可以存储自定义数据。
 */
interface IData {

    val name: String

    val root: IDataRoot

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
     * Clear the [IData]
     */
    fun clear()

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
     * Get a Map containing all keys and their values for this [IData].
     *
     *
     * If deep is set to true, then this will contain all the keys and values
     * within any child [IData]s (and their children, etc.).
     * These keys will be in a valid path notation for you to use.
     *
     *
     * If deep is set to false, then this will contain only the keys and
     * values of any direct children, and not their own children.
     *
     * @param deep Whether to get a deep list, as opposed to a shallow list.
     * @return Map of keys and values of this [IData].
     */
    fun getValues(deep: Boolean): Map<String, Any>

    /**
     * Check if this [IData] contains the given path.
     *
     * @param path Path to check for existence.
     * @return True if this [IData] contains the requested path.
     */
    operator fun contains(path: String): Boolean

    /**
     * Get the requested String by path.
     *
     *
     * If the String does not exist, this will return null.
     *
     * @param path Path of the String to get.
     * @return Requested String.
     */
    fun getString(path: String): String?

    /**
     * Get the requested String by path, returning a default value if not
     * found.
     *
     * @param path Path of the String to get.
     * @param default The default value to return if the path is not found or is
     * not a String.
     * @return Requested String.
     */
    fun getString(path: String, default: String): String

    /**
     * Check if the specified path is a String.
     *
     *
     * If the path exists but is not a String, this will return false. If the
     * path does not exist, this will return false.
     *
     * @param path Path of the String to check.
     * @return Whether the specified path is a String.
     */
    fun isString(path: String): Boolean

    /**
     * Get the requested int by path.
     *
     *
     * If the int does not exist, this will return 0.
     *
     * @param path Path of the int to get.
     * @return Requested int.
     */
    fun getInt(path: String): Int

    /**
     * Get the requested int by path, returning a default value if not found.
     *
     * @param path Path of the int to get.
     * @param default The default value to return if the path is not found or is
     * not an int.
     * @return Requested int.
     */
    fun getInt(path: String, default: Int): Int

    /**
     * Check if the specified path is an int.
     *
     *
     * If the path exists but is not an int, this will return false. If the
     * path does not exist, this will return false.
     *
     * @param path Path of the int to check.
     * @return Whether the specified path is an int.
     */
    fun isInt(path: String): Boolean

    /**
     * Get the requested boolean by path.
     *
     *
     * If the boolean does not exist, this will return false.
     *
     * @param path Path of the boolean to get.
     * @return Requested boolean.
     */
    fun getBoolean(path: String): Boolean

    /**
     * Get the requested boolean by path, returning a default value if not
     * found.
     *
     * @param path Path of the boolean to get.
     * @param default The default value to return if the path is not found or is
     * not a boolean.
     * @return Requested boolean.
     */
    fun getBoolean(path: String, default: Boolean): Boolean

    /**
     * Check if the specified path is a boolean.
     *
     *
     * If the path exists but is not a boolean, this will return false. If the
     * path does not exist, this will return false.
     *
     * @param path Path of the boolean to check.
     * @return Whether the specified path is a boolean.
     */
    fun isBoolean(path: String): Boolean

    /**
     * Get the requested double by path.
     *
     *
     * If the double does not exist, this will return 0.
     *
     * @param path Path of the double to get.
     * @return Requested double.
     */
    fun getDouble(path: String): Double

    /**
     * Get the requested double by path, returning a default value if not
     * found.
     *
     * @param path Path of the double to get.
     * @param default The default value to return if the path is not found or is
     * not a double.
     * @return Requested double.
     */
    fun getDouble(path: String, default: Double): Double

    /**
     * Check if the specified path is a double.
     *
     *
     * If the path exists but is not a double, this will return false. If the
     * path does not exist, this will return false.
     *
     * @param path Path of the double to check.
     * @return Whether the specified path is a double.
     */
    fun isDouble(path: String): Boolean

    /**
     * Get the requested long by path.
     *
     *
     * If the long does not exist, this will return 0.
     *
     * @param path Path of the long to get.
     * @return Requested long.
     */
    fun getLong(path: String): Long

    /**
     * Get the requested long by path, returning a default value if not
     * found.
     *
     * @param path Path of the long to get.
     * @param default The default value to return if the path is not found or is
     * not a long.
     * @return Requested long.
     */
    fun getLong(path: String, default: Long): Long

    /**
     * Check if the specified path is a long.
     *
     *
     * If the path exists but is not a long, this will return false. If the
     * path does not exist, this will return false.
     *
     * @param path Path of the long to check.
     * @return Whether the specified path is a long.
     */
    fun isLong(path: String): Boolean

    /**
     * Get the requested List by path.
     *
     *
     * If the List does not exist, this will return null.
     *
     * @param path Path of the List to get.
     * @return Requested List.
     */
    fun getList(path: String): List<*>?

    /**
     * Get the requested List by path, returning a default value if not
     * found.
     *
     * @param path Path of the List to get.
     * @param default The default value to return if the path is not found or is
     * not a List.
     * @return Requested List.
     */
    fun getList(path: String, default: List<*>): List<*>

    /**
     * Check if the specified path is a List.
     *
     *
     * If the path exists but is not a List, this will return false. If the
     * path does not exist, this will return false.
     *
     * @param path Path of the List to check.
     * @return Whether the specified path is a List.
     */
    fun isList(path: String): Boolean

    /**
     * Get the requested List of String by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a String if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of String.
     */
    fun getStringList(path: String): List<String>

    /**
     * Get the requested List of Int by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into an Int if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Integer.
     */
    fun getIntList(path: String): List<Int>

    /**
     * Get the requested List of Boolean by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Boolean if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Boolean.
     */
    fun getBooleanList(path: String): List<Boolean>

    /**
     * Get the requested List of Double by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Double if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Double.
     */
    fun getDoubleList(path: String): List<Double>

    /**
     * Get the requested List of Float by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Float if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Float.
     */
    fun getFloatList(path: String): List<Float>

    /**
     * Get the requested List of Long by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Long if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Long.
     */
    fun getLongList(path: String): List<Long>

    /**
     * Get the requested List of Byte by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Byte if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Byte.
     */
    fun getByteList(path: String): List<Byte>

    /**
     * Get the requested List of Char by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Char if
     * possible, but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Character.
     */
    fun getCharList(path: String): List<Char>

    /**
     * Get the requested List of Short by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Short if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Short.
     */
    fun getShortList(path: String): List<Short>

    /**
     * Get the requested List of Maps by path.
     *
     *
     * If the List does not exist, this will return an empty List.
     *
     *
     * This method will attempt to cast any values into a Map if possible, but
     * may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Maps.
     */
    fun getMapList(path: String): List<Map<*, *>>


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
         * the given relative [IData], or from its
         * [IData.root] when [relativeTo] is null.
         *
         * @param data [IData] to create a path for.
         * @param key Name of the specified [IData].
         * @param relativeTo [IData] to create the path relative to.
         * @return Full path of the [data] from its root.
         * Empty when the roots of [data] and [relativeTo] are different.
         */
        @JvmStatic
        @JvmOverloads
        fun createPath(data: IData, key: String, relativeTo: IData? = null): String {
            val root = data.root
            if (relativeTo != null && root !== relativeTo.root)
                return ""

            val separator = root.options.pathSeparator

            val builder = StringBuilder()
            var parent: IData? = data
            while (parent != null && parent !== relativeTo) {
                if (builder.isNotEmpty())
                    builder.insert(0, separator)

                builder.insert(0, parent.name)
                parent = parent.parent
            }

            if (key.isNotEmpty()) {
                if (builder.isNotEmpty())
                    builder.append(separator)

                builder.append(key)
            }

            return builder.toString()
        }
    }

}