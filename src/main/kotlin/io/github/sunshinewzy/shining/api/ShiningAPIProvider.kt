package io.github.sunshinewzy.shining.api

object ShiningAPIProvider {
    
    internal var api: IShiningAPI? = null
    
    fun api(): IShiningAPI {
        return api ?: throw IllegalStateException("ShiningAPI has not finished loading, or failed to load!")
    }
    
}