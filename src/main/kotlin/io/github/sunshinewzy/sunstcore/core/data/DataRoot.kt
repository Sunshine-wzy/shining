package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer

class DataRoot(
    name: String,
    override val container: IDataContainer
) : Data(name), IDataRoot {
    
    override var options: DataRootOptions = DataRootOptions()
    
}