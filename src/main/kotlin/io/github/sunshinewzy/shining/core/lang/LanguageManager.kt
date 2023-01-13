package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.ShiningConfig
import io.github.sunshinewzy.shining.api.lang.ILanguageManager
import io.github.sunshinewzy.shining.api.lang.TextTransfer
import io.github.sunshinewzy.shining.core.lang.transfer.ColorTextTransfer
import taboolib.common.platform.function.warning
import java.io.File
import java.util.jar.JarFile

abstract class LanguageManager(val jarFile: File) : ILanguageManager {
    
    protected val languageCode: HashSet<String> = HashSet()
    protected val languageFileMap: HashMap<String, LanguageFile> = HashMap()

    val textTransfer: MutableList<TextTransfer> = ArrayList()
    
    
    init {
        // Load the language code
        JarFile(jarFile).use { jar ->
            jar.entries().iterator().forEachRemaining {
                if (it.name.startsWith("lang/") && it.name.endsWith(".yml")) {
                    languageCode += it.name.substringAfter('/').substringBeforeLast('.')
                }
            }
        }
        
        // Load transfer
        textTransfer += ColorTextTransfer
    }
    
    
    override fun getLanguageCode(): Set<String> = languageCode
    
    override fun getLanguageFileMap(): Map<String, LanguageFile> = languageFileMap

    override fun getLanguageFile(locale: String): LanguageFile? {
        val localeLowercase = locale.lowercase()
        return languageFileMap[localeLowercase] ?: kotlin.run { 
            languageCodeMap[localeLowercase]?.let { 
                languageFileMap[it.lowercase()]
            } ?: languageFileMap[ShiningConfig.language.lowercase()] ?: kotlin.run {
                warning(
                    "The default language file '${ShiningConfig.language}.yml' is missing.",
                    "Please check the language file or change the default language."
                )
                null
            }
        }
    }

    override fun transfer(source: String): String {
        var text = source
        textTransfer.forEach { 
            text = it.transfer(text)
        }
        return text
    }
    

    companion object {
        val languageCodeMap: Map<String, String> = hashMapOf(
            "zh_hans_cn" to "zh_CN",
            "zh_hant_cn" to "zh_TW",
            "en_ca" to "en_US",
            "en_au" to "en_US",
            "en_gb" to "en_US",
            "en_nz" to "en_US"
        )
    }
    
}