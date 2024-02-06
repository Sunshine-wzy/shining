package io.github.sunshinewzy.shining.api.blueprint;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface IBlueprintClass {
	
	@NotNull
	ArrayList<IBlueprintNodeTree> getNodeTrees();
	
	void edit(@NotNull Player player);
	
}
