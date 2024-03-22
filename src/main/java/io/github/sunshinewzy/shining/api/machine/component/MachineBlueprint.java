package io.github.sunshinewzy.shining.api.machine.component;

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass;
import io.github.sunshinewzy.shining.api.machine.AbstractMachineComponent;
import io.github.sunshinewzy.shining.api.machine.IMachine;
import org.jetbrains.annotations.NotNull;

public class MachineBlueprint extends AbstractMachineComponent {

	private IBlueprintClass blueprintClass;
	
	public MachineBlueprint(@NotNull IMachine machine) {
		super(machine);
	}
	
}
