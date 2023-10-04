package io.github.sunshinewzy.shining.api.objects

import java.util.*

/**
 * Represents a generic pair of two values.
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Pair exhibits value semantics, i.e. two pairs are equal if both components are equal.
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @property first First value.
 * @property second Second value.
 * @constructor Creates a new instance of Pair.
 */
data class SPair<out A, out B>(val first: A, val second: B) {
    /**
     * Returns string representation of the [Pair] including its [first] and [second] values.
     */
    override fun toString(): String = "($first, $second)"
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SPair<*, *>) return false
        
        if (!Objects.equals(first, other.first)) return false
        if (!Objects.equals(second, other.second)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        return result
    }
}
