package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer

class SerialData(
    name: String,
    override val container: ISerialDataContainer
) : Data(name, container) {


}