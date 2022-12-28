package io.github.sunshinewzy.shining.exceptions

open class MachineStructureException(structure: String, reason: String) : RuntimeException(
    """
        The machine structure:
        ----------------------
        $structure
        ----------------------
        is illegal.

        Result from:
        $reason
    """.trimIndent()
)