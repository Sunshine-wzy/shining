package io.github.sunshinewzy.shining.commands

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault

@CommandHeader("shiningapi", permissionDefault = PermissionDefault.TRUE)
object ShiningCommandAPI {

    @CommandBody
    val editor = CommandEditor.editor
    
}