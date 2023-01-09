package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.addon.ShiningAddon

class AddonLanguageManager(val addon: ShiningAddon) : LanguageManager(addon.file) {

    override fun reload() {
        languageFile.clear()
        languageFile.putAll(LanguageFileLoader.loadLanguageFiles(getLanguageCode(), addon.javaClass.classLoader))
    }
    
}