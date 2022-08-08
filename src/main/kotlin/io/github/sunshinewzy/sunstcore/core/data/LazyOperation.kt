package io.github.sunshinewzy.sunstcore.core.data

sealed class LazyOperation {
    class Update<T>(val value: T) : LazyOperation()

    object Delete : LazyOperation()
}