package io.github.sunshinewzy.sunstcore.api

import io.github.sunshinewzy.sunstcore.api.namespace.Namespace

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
        return Namespace[getName().lowercase()]
    }
    
}