package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.tick.Tickable

/**
 * Machine represents a block or a collection of blocks which can be interacted and has its own state.
 */
interface IMachine : Tickable {

    val property: MachineProperty


    fun run()

    fun edit()

}