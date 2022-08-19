package io.github.sunshinewzy.sunstcore.api.data

import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import io.github.sunshinewzy.sunstcore.core.data.container.DataRootOptions

interface IDataRoot : IData {

    val container: IDataContainer

    val options: DataRootOptions
    
}