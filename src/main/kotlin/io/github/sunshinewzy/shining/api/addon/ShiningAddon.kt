package io.github.sunshinewzy.shining.api.addon

import io.github.sunshinewzy.shining.api.ShiningPlugin
import java.io.File
import java.util.logging.Logger

abstract class ShiningAddon : ShiningPlugin {

    lateinit var logger: Logger
        private set
    lateinit var file: File
        private set
    lateinit var dataFolder: File
        private set


    open fun onInit() {}

    open fun onLoad() {}

    open fun onEnable() {}

    open fun onActive() {}

    open fun onDisable() {}


    fun init() {

    }

}