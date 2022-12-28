package io.github.sunshinewzy.shining.exceptions

import org.bukkit.Location

class NoEnergyUnitException(loc: Location) : RuntimeException(
    "(${loc.x}, ${loc.y}, ${loc.z}) is not in a energy unit"
)