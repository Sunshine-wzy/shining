package io.github.sunshinewzy.sunstcore.core.guide.data

import io.github.sunshinewzy.sunstcore.core.guide.ElementCondition
import kotlinx.serialization.Serializable

@Serializable
data class ElementPlayerData(
    var condition: ElementCondition = ElementCondition.LOCKED_LOCK 
) {
    
    
    
}