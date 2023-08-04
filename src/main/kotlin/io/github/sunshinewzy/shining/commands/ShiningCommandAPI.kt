package io.github.sunshinewzy.shining.commands

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault

@CommandHeader("shiningapi", permission = "shiningapi", permissionDefault = PermissionDefault.TRUE)
object ShiningCommandAPI {

    @CommandBody(permission = "shiningapi.editor", permissionDefault = PermissionDefault.TRUE)
    val editor = CommandEditor.editor

}