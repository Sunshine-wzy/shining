package io.github.sunshinewzy.sunstcore.core.machine

import io.github.sunshinewzy.sunstcore.api.Namespace

/**
 * Represent properties of a machine.
 */
data class MachineProperty(
    val namespace: Namespace,
    val id: String
)