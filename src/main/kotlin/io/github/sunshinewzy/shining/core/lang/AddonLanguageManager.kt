package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.addon.ShiningAddon

class AddonLanguageManager(val addon: ShiningAddon) : LanguageManager(addon.file) {
    
    init {
        ShiningLanguageManager.registerAddonLanguageManager(this)
    }
    
    override fun reload() {
        languageFileMap.clear()
        languageFileMap.putAll(LanguageFileLoader.loadLanguageFiles(getLanguageCode(), addon.javaClass.classLoader))
    }
    
}