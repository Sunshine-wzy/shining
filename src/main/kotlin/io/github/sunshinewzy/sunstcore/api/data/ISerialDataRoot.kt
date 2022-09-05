package io.github.sunshinewzy.sunstcore.api.data

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer

interface ISerialDataRoot : ISerialData, IDataRoot {
    
    @get:JsonIgnore
    override val container: ISerialDataContainer
    
}