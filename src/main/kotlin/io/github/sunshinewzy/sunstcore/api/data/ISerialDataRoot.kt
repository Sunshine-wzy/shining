package io.github.sunshinewzy.sunstcore.api.data

import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer

interface ISerialDataRoot : ISerialData, IDataRoot {
    
    override val container: ISerialDataContainer
    
}