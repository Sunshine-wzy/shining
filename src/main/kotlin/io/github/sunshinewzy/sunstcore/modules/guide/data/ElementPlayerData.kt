package io.github.sunshinewzy.sunstcore.modules.guide.data

import io.github.sunshinewzy.sunstcore.modules.guide.ElementCondition
import kotlinx.serialization.Serializable

@Serializable
data class ElementPlayerData(
    var condition: ElementCondition = ElementCondition.LOCKED_LOCK 
) {
    
    
    
}