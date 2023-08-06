package io.github.sunshinewzy.shining.core.addon.loader

class ShiningAddonInitializingException(
    val loader: ShiningAddonJarLoader,
    th: Throwable
) : RuntimeException("Failed to initialize addon '${loader.description.getNameAndVersion()}'", th) 