package io.github.sunshinewzy.shining.api.blueprint;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum BlueprintNodeType {
	EVENT(Material.OAK_BUTTON, "blueprint-node-type-event"),
	FUNCTION(Material.REDSTONE_LAMP, "blueprint-node-type-function"),
	FLOW_CONTROL(Material.REPEATER, "blueprint-node-type-flow_control"),
	VARIABLE(Material.PAPER, "blueprint-node-type-variable");
	
	private Material material;
	private String languageItemId;
	
	BlueprintNodeType(@NotNull Material material, @NotNull String languageItemId) {
		this.material = material;
		this.languageItemId = languageItemId;
	}

	public String getLanguageItemId() {
		return languageItemId;
	}

	public Material getMaterial() {
		return material;
	}
}
