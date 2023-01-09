package io.github.sunshinewzy.shining.api.lang

import io.github.sunshinewzy.shining.core.lang.LanguageFile

interface ILanguageManager {
    
    fun reload()

    fun getLanguageCode(): Set<String>

    fun getLanguageFile(): Map<String, LanguageFile>
    
}