package io.github.sunshinewzy.shining.api

import taboolib.module.configuration.ConfigNode

object ShiningConfig {

    @ConfigNode("options.language")
    var language: String = "zh_CN"
        private set
    
    @ConfigNode("options.default_repository_central")
    var defaultRepositoryCentral = "https://maven.aliyun.com/repository/central"
        private set

}