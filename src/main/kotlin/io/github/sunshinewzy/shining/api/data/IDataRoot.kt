package io.github.sunshinewzy.shining.api.data

import io.github.sunshinewzy.shining.api.data.container.IDataContainer
import io.github.sunshinewzy.shining.core.data.DataRootOptions

interface IDataRoot : IData {

    val container: IDataContainer

    var options: DataRootOptions

}