package io.github.sunshinewzy.sunstcore.modules.data

/**
 * Represent the object will be stored in the database which has two columns.
 * One is the [key], and the other is the bytes of the object which will be serialized by [kotlinx.serialization].
 * 
 * The class which implements this interface must be annotated with @Serializable annotation, or it will throw an exception.
 */
interface KeySerializable {
    /**
     * The key is the primary key in the database.
     * Make sure it is unique.
     */
    fun key(): String
}