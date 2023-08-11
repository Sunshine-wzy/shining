package io.github.sunshinewzy.shining.api.data

import io.github.sunshinewzy.shining.api.data.container.IDataContainer

interface IDataRoot : IData {

    val container: IDataContainer

    var options: DataRootOptions

}