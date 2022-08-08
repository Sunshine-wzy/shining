package io.github.sunshinewzy.sunstcore.core.machine.legacy

enum class SMachineStatus(val flag: Boolean) {
    START(true),
    RUNNING(true),
    FINISH(false)
}