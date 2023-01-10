package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.ShiningConfig
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.node.LanguageNode
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getJarFile

object ShiningLanguageManager : LanguageManager(getJarFile()) {
    
    private val addonLanguageManagerMap: MutableMap<Namespace, AddonLanguageManager> = HashMap()
    
    
    @Awake(LifeCycle.INIT)
    override fun reload() {
        languageFileMap.clear()
        languageFileMap.putAll(LanguageFileLoader.loadLanguageFiles(getLanguageCode(), Shining.javaClass.classLoader))
    }
    
    
    fun registerAddonLanguageManager(manager: AddonLanguageManager) {
        addonLanguageManagerMap[manager.addon.getNamespace()] = manager
    }
    
    @JvmOverloads
    fun getLanguageNode(namespacedId: NamespacedId, prefix: String = "", locale: String = ShiningConfig.language): LanguageNode? {
        if(namespacedId.namespace == Shining.getNamespace()) {
            return getLanguageNode(locale, namespacedId.toNodeString(prefix))
        }
        return addonLanguageManagerMap[namespacedId.namespace]?.getLanguageNode(locale, namespacedId.toNodeString(prefix))
    }

}