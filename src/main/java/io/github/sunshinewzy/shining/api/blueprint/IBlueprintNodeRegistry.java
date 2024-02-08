package io.github.sunshinewzy.shining.api.blueprint;

import io.github.sunshinewzy.shining.api.lang.item.ILanguageItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IBlueprintNodeRegistry {
	
	@NotNull
	List<IBlueprintNode> get(@NotNull BlueprintNodeType type);
	
	void register(@NotNull BlueprintNodeType type, @NotNull IBlueprintNode node);
	
	@NotNull
	ILanguageItem getTypeIcon(@NotNull BlueprintNodeType type);
	
}
