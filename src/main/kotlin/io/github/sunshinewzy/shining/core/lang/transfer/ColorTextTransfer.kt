package io.github.sunshinewzy.shining.core.lang.transfer

import io.github.sunshinewzy.shining.api.lang.transfer.TextTransfer
import taboolib.module.chat.colored

object ColorTextTransfer : TextTransfer {

    override fun transfer(source: String): String {
        return source.colored()
    }
    
}