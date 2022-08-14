package io.github.sunshinewzy.sunstcore.api.data

import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer

/**
 * Represent a [Map] like object, capable of storing custom data persistently in it.
 * 
 * 
 * 表示一个类似 [Map] 的对象，可以持久化存储自定义数据。
 */
interface IData {
    
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
    
    
    fun getData(path: String): IData?
    
    
    
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
    }
    
}