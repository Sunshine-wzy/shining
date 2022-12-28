package io.github.sunshinewzy.shining.core.data

sealed class LazyOperation {
    class Update<T>(val value: T) : LazyOperation()

    object Delete : LazyOperation()
}