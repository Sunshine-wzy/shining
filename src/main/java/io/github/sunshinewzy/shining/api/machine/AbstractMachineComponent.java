package io.github.sunshinewzy.shining.api.machine;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractMachineComponent implements IMachineComponent {

	private final IMachine machine;

	public AbstractMachineComponent(@NotNull IMachine machine) {
		this.machine = machine;
	}

	@NotNull
	@Override
	public IMachine getMachine() {
		return machine;
	}
	
}
