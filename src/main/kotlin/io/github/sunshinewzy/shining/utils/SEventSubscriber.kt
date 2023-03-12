package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.objects.SingleListener
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

object SEventSubscriber {

    private val subscribers =
        HashMap<Triple<String, EventPriority, Boolean>, ArrayList<SEventSubscriberWrapper<out Event>>>()


    fun <E : Event> subscribeEvent(
        eventClass: Class<out E>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        block: E.() -> Unit
    ) {
        val key = Triple(eventClass.name, priority, ignoreCancelled)
        val eventBlock = SEventSubscriberWrapper(block)
        val list = subscribers[key]

        if (list == null) {
            SingleListener.listen(eventClass, priority, ignoreCancelled) {
                callSubscribeEvent(it, key)
            }
            subscribers[key] = arrayListOf(eventBlock)
        } else list += eventBlock
    }

    fun <E : Event> callSubscribeEvent(event: E, key: Triple<String, EventPriority, Boolean>) {
        val list = subscribers[key]
        list?.forEach {
            @Suppress("UNCHECKED_CAST")
            val block = it.block as E.() -> Unit
            block(event)
        }
    }

}

class SEventSubscriberWrapper<E : Event> internal constructor(
    val block: E.() -> Unit
)


inline fun <reified E : Event> subscribeEvent(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    noinline block: E.() -> Unit
) = subscribeEvent(E::class.java, priority, ignoreCancelled, block)

fun <E : Event> subscribeEvent(
    eventClass: Class<out E>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    block: E.() -> Unit
) {
    SEventSubscriber.subscribeEvent(eventClass, priority, ignoreCancelled, block)
}
