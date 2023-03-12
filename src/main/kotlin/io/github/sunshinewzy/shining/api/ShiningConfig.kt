package io.github.sunshinewzy.shining.api

import taboolib.module.configuration.ConfigNode

object ShiningConfig {

    @ConfigNode("options.language")
    var language: String = "zh_CN"
        private set

}