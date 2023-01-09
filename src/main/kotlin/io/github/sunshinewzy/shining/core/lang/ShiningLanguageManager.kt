package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.Shining
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getJarFile

object ShiningLanguageManager : LanguageManager(getJarFile()) {
    
    @Awake(LifeCycle.INIT)
    override fun reload() {
        languageFile.clear()
        languageFile.putAll(LanguageFileLoader.loadLanguageFiles(getLanguageCode(), Shining.javaClass.classLoader))
    }

}