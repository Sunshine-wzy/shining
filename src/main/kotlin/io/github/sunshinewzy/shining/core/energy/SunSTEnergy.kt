package io.github.sunshinewzy.shining.core.energy

class SunSTEnergy(var energy: Long = 0) : SEnergyUnit {

    override fun plus(unit: SEnergyUnit): SEnergyUnit {
        return SunSTEnergy(energy + unit.toSunSTEnergy().energy)
    }

    override fun minus(unit: SEnergyUnit): SEnergyUnit {
        return SunSTEnergy(energy - unit.toSunSTEnergy().energy)
    }

    override fun plusAssign(unit: SEnergyUnit) {
        energy += unit.toSunSTEnergy().energy
    }

    override fun minusAssign(unit: SEnergyUnit) {
        energy -= unit.toSunSTEnergy().energy
    }

    override fun toSunSTEnergy(): SunSTEnergy = this
}