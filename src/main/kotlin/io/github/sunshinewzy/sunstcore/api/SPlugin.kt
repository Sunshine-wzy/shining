package io.github.sunshinewzy.sunstcore.api

interface SPlugin {

    /**
     * Name of the plugin.
     */
    fun getName(): String


    /**
     * The namespace may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     */
    fun getNamespace(): Namespace {
        return Namespace.get(getName().lowercase())
    }
    
}