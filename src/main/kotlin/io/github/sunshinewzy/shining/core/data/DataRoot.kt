package io.github.sunshinewzy.shining.core.data

import io.github.sunshinewzy.shining.api.data.IDataRoot
import io.github.sunshinewzy.shining.api.data.container.IDataContainer

class DataRoot(
    name: String,
    override val container: IDataContainer
) : Data(name), IDataRoot {
    
    override var options: DataRootOptions = DataRootOptions()
    
}