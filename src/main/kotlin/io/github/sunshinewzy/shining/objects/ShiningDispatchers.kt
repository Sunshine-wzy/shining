package io.github.sunshinewzy.shining.objects

import io.github.sunshinewzy.shining.Shining
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object ShiningDispatchers {
    
    val SQL: ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()


    fun launchSQL(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return if (context == EmptyCoroutineContext) Shining.coroutineScope.launch(SQL, start, block)
        else Shining.coroutineScope.launch(context + SQL, start, block)
    }

    fun launchIO(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return if (context == EmptyCoroutineContext) Shining.coroutineScope.launch(Dispatchers.IO, start, block)
        else Shining.coroutineScope.launch(context + Dispatchers.IO, start, block)
    }
    
}