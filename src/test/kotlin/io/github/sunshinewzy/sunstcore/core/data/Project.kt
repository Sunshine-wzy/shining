package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.core.data.container.SerialDataContainer

data class Project1(
    val name: String,
    val id: Long
)

data class Project2(
    val name: String,
    val id: Long,
    val container: ISerialDataContainer = SerialDataContainer()
)