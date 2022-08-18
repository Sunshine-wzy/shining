package io.github.sunshinewzy.sunstcore.api.data

import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer

interface ISerialData : IData {
    override val container: ISerialDataContainer
    
    
}