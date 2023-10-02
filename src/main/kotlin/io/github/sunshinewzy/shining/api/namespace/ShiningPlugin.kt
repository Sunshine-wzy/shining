package io.github.sunshinewzy.shining.api.namespace

interface ShiningPlugin {

    /**
     * Name of the plugin.
     */
    fun getName(): String

    /**
     * The namespace may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     */
    fun getNamespace(): Namespace = Namespace.get(getName().lowercase())

    fun getPrefix(): String = getName()

}