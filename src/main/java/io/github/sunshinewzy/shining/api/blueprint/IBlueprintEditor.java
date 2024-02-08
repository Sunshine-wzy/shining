package io.github.sunshinewzy.shining.api.blueprint;

import io.github.sunshinewzy.shining.api.guide.context.EmptyGuideContext;
import io.github.sunshinewzy.shining.api.guide.context.GuideContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBlueprintEditor {
	
	void open(@NotNull Player player, @Nullable IBlueprintClass blueprint);
	
	default void open(@NotNull Player player) {
		open(player, null);
	}
	
	void openNodeSelector(@NotNull Player player, @NotNull GuideContext context);
	
	default void openNodeSelector(@NotNull Player player) {
		openNodeSelector(player, EmptyGuideContext.INSTANCE);
	}
	
}
