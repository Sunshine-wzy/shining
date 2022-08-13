package io.github.sunshinewzy.sunstcore.core.data

/**
 * Represent a [Map] like object, capable of storing custom data persistently in it.
 * 
 * 
 * 表示一个类似 [Map] 的对象，可以持久化存储自定义数据。
 */
interface IData {
    
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
    
    
    
    companion object {
        inline fun <reified T> IData.getByType(path: String): T? {
            get(path)?.let { 
                if(it is T) return it
            }
            
            return null
        }
    }
    
}