package io.github.sunshinewzy.shining.core.universal.block

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData

@JsonTypeName("vanilla")
class VanillaUniversalBlock(val data: BlockData) : UniversalBlock {
    
    @JsonCreator
    constructor(@JsonProperty("data") source: String) : this(Bukkit.createBlockData(source))
    
}