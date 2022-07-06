package io.github.sunshinewzy.sunstcore.modules.machine

import io.github.sunshinewzy.sunstcore.objects.SCoordinate

enum class SMachineSize(val size: Int) {
    SIZE3(3),
    SIZE5(5),
    
    ;
    
    
    fun isCoordInSize(coord: SCoordinate): Boolean {
        val (x, y, z) = coord
        val max = size / 2
        
        if(y in 0 until size && x in -max..max && z in -max..max)
            return true
        
        return false
    }
}