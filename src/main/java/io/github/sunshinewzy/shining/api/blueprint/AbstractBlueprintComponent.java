package io.github.sunshinewzy.shining.api.blueprint;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlueprintComponent implements IBlueprintComponent {

	private final IBlueprintClass blueprint;

	public AbstractBlueprintComponent(@NotNull IBlueprintClass blueprint) {
		this.blueprint = blueprint;
	}

	@NotNull
	@Override
	public IBlueprintClass getBlueprint() {
		return blueprint;
	}
	
}
