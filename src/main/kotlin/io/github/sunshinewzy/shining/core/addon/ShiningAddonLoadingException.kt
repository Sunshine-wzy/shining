package io.github.sunshinewzy.shining.core.addon

class ShiningAddonLoadingException(
    val name: String,
    val reason: String
) : RuntimeException("Failed to load addon '$name'. Reason: $reason") 