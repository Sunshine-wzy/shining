package io.github.sunshinewzy.sunstcore.exceptions

import org.bukkit.Location

class NoEnergyUnitException(loc: Location) : RuntimeException(
    "(${loc.x}, ${loc.y}, ${loc.z}) is not in a energy unit"
)