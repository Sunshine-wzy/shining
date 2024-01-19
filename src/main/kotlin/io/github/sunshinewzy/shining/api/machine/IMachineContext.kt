package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.objects.position.Position3D

interface IMachineContext {

    /**
     * The absolute position of the center block of the machine
     */
    val position: Position3D
    
}