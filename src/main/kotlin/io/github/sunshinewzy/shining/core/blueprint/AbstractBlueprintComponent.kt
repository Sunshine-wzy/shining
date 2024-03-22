package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintComponent

abstract class AbstractBlueprintComponent(private val blueprint: IBlueprintClass) : IBlueprintComponent {

    override fun getBlueprint(): IBlueprintClass = blueprint
    
}