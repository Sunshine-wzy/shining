package io.github.sunshinewzy.sunstcore.modules.data

sealed class LazyOperation {
    class Update<T>(val value: T) : LazyOperation()

    object Delete : LazyOperation()
}