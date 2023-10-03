package io.github.sunshinewzy.shining.objects

import io.github.sunshinewzy.shining.Shining
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object ShiningDispatchers {
    
    val DB: ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor(
        object : ThreadFactory {
            private val group = Thread.currentThread().threadGroup
            private val threadNumber = AtomicInteger(1)
            private val namePrefix = "Database-thread-"
            
            override fun newThread(r: Runnable): Thread {
                val t = Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0)
                
                if (t.isDaemon) t.isDaemon = false
                if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
                return t
            }
        }
    ).asCoroutineDispatcher()


    fun launchDB(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return if (context == EmptyCoroutineContext) Shining.coroutineScope.launch(DB, start, block)
        else Shining.coroutineScope.launch(context + DB, start, block)
    }

    fun launchIO(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return if (context == EmptyCoroutineContext) Shining.coroutineScope.launch(Dispatchers.IO, start, block)
        else Shining.coroutineScope.launch(context + Dispatchers.IO, start, block)
    }
    
    fun <T> futureDB(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): CompletableFuture<T> {
        return if (context == EmptyCoroutineContext) Shining.coroutineScope.future(DB, start, block)
        else Shining.coroutineScope.future(context + DB, start, block)
    }

    fun <T> futureIO(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): CompletableFuture<T> {
        return if (context == EmptyCoroutineContext) Shining.coroutineScope.future(Dispatchers.IO, start, block)
        else Shining.coroutineScope.future(context + Dispatchers.IO, start, block)
    }
    
}