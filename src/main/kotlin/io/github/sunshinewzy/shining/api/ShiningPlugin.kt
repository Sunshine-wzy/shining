package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.api.namespace.Namespace

interface ShiningPlugin {

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