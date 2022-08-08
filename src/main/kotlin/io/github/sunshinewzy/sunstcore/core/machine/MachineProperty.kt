package io.github.sunshinewzy.sunstcore.core.machine

import io.github.sunshinewzy.sunstcore.api.Namespace
import kotlinx.serialization.Serializable

/**
 * Represent properties of a machine.
 */
@Serializable
data class MachineProperty(
    val namespace: Namespace,
    val id: String
)