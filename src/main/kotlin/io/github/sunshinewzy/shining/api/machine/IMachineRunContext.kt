package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
import io.github.sunshinewzy.shining.api.objects.position.Position3D

interface IMachineRunContext : IMachineContext {

    /**
     * The absolute position of the block which triggers the machine to run
     */
    val runPosition: Position3D

    /**
     * The coordinate of the block which triggers the machine to run relative to the center
     */
    val runCoordinate: Coordinate3D
    
}