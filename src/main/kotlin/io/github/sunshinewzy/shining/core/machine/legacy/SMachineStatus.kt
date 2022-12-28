package io.github.sunshinewzy.shining.core.machine.legacy

enum class SMachineStatus(val flag: Boolean) {
    START(true),
    RUNNING(true),
    FINISH(false)
}