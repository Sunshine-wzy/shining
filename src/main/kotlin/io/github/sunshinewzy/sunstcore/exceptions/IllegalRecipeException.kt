package io.github.sunshinewzy.sunstcore.exceptions

class IllegalRecipeException(shape: String, reason: String) : RuntimeException(
    """
        The recipe:
        ----------------------
        $shape
        ----------------------
        is illegal.

        Result from:
        $reason
    """.trimIndent()
)