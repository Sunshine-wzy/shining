package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.lang.ILanguageManager
import java.io.File
import java.util.jar.JarFile

abstract class LanguageManager(val jarFile: File) : ILanguageManager {
    
    protected val languageCode: HashSet<String> = HashSet()
    protected val languageFile: HashMap<String, LanguageFile> = HashMap()

    val languageCodeTransfer: HashMap<String, String> = hashMapOf(
        "zh_hans_cn" to "zh_CN",
        "zh_hant_cn" to "zh_TW",
        "en_ca" to "en_US",
        "en_au" to "en_US",
        "en_gb" to "en_US",
        "en_nz" to "en_US"
    )
    
    
    init {
        // Load the language code
        JarFile(jarFile).use { jar ->
            jar.entries().iterator().forEachRemaining {
                if (it.name.startsWith("lang/") && it.name.endsWith(".yml")) {
                    languageCode += it.name.substringAfter('/').substringBeforeLast('.')
                }
            }
        }
        
        // TODO: Load transfers
        
    }
    
    
    override fun getLanguageCode(): Set<String> = languageCode
    
    override fun getLanguageFile(): Map<String, LanguageFile> = languageFile
    
}