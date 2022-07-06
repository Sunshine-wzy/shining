package io.github.sunshinewzy.sunstcore.exceptions

class NoIngredientException(structure: String, char: Char) : MachineStructureException(
    structure, "Char \'$char\' doesn't have a ingredient definition"
)