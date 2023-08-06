package io.github.sunshinewzy.shining.core.addon

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.utils.ServerUtils
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

class ShiningAddonLogger(name: String) : Logger("shining", null) {

    // On Paper, [logger.name] is appended in front of each log message
    private val prefix = if (ServerUtils.SERVER_SOFTWARE.isPaper()) "[$name] " else "[shining] [$name] "

    init {
        parent = Shining.plugin.logger
        level = Level.ALL
    }

    override fun log(record: LogRecord) {
        record.message = prefix + record.message
        super.log(record)
    }
    
}