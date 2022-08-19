package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import io.github.sunshinewzy.sunstcore.core.data.container.DataRootOptions

class DataRoot(
    name: String,
    override val container: IDataContainer
) : Data(name), IDataRoot {
    override val options: DataRootOptions = DataRootOptions()
    
    
    
}