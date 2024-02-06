package io.github.sunshinewzy.shining.api.blueprint;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBlueprintEditor {
	
	void open(@NotNull Player player, @Nullable IBlueprintClass blueprint);
	
	default void open(@NotNull Player player) {
		open(player, null);
	}
	
}
