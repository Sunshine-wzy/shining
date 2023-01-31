package io.github.sunshinewzy.shining.utils

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponentDynamic
import java.util.*

fun CommandComponentDynamic.restrictUUID(): CommandComponentDynamic =
    restrict<ProxyCommandSender> { _, _, argument ->
        kotlin.runCatching { UUID.fromString(argument) }
            .onSuccess { 
                return@restrict true
            }
        
        false
    }