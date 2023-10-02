package io.github.sunshinewzy.shining.core.data

import io.github.sunshinewzy.shining.core.data.container.IDataContainer

interface IDataRoot : IData {

    val container: IDataContainer

    var options: DataRootOptions

}