package io.github.sunshinewzy.shining.core.addon

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.addon.ShiningAddon
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import java.io.File

@SkipTo(LifeCycle.ENABLE)
object ShiningAddonManager {
    
    val addonsFolder: File = File(Shining.nativeDataFolder(), "addons/")
    internal val addons: MutableMap<String, ShiningAddon> = LinkedHashMap()
    
    
    init {
        addonsFolder.mkdirs()
    }
    
    
    fun hasAddon(id: String): Boolean = id in addons
    
    fun getAddon(id: String): ShiningAddon? = addons[id]
    
    
    internal fun loadAddons() {
        addonsFolder.listFiles()?.forEach { file -> 
            if (file.isFile && file.extension == "jar") {
                try {
                    val loader = ShiningAddonJarLoader(file)
                    
                    
                    
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
    
}