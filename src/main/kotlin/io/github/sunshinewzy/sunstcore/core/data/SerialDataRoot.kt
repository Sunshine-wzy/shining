package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.core.data.container.DataRootOptions

class SerialDataRoot(
    name: String,
    override val container: ISerialDataContainer
) : SerialData(name), ISerialDataRoot {

    override val options: DataRootOptions = DataRootOptions()
    
}