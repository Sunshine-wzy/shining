package io.github.sunshinewzy.sunstcore.modules.machine

enum class SMachineStatus(val flag: Boolean) {
    START(true),
    RUNNING(true),
    FINISH(false)
}