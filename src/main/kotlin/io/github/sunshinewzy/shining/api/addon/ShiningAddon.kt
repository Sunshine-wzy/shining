package io.github.sunshinewzy.shining.api.addon

import io.github.sunshinewzy.shining.api.ShiningPlugin
import java.io.File
import java.util.logging.Logger

abstract class ShiningAddon : ShiningPlugin {

    lateinit var logger: Logger internal set
    lateinit var file: File internal set
    lateinit var dataFolder: File internal set
    lateinit var description: ShiningAddonJarDescription internal set

    override fun getName(): String = description.name

    
    open fun onInit() {}

    open fun onEnable() {}

    open fun onActive() {}

    open fun onDisable() {}

}