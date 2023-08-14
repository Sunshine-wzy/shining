package io.github.sunshinewzy.shining.api.dictionary;

import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior;
import io.github.sunshinewzy.shining.api.namespace.NamespacedId;
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDictionaryRegistry {

	@Nullable
	DictionaryItem get(@NotNull NamespacedId name);

	@Nullable
	DictionaryItem get(@NotNull ItemStack item);

	@NotNull
	DictionaryItem getOrFail(@NotNull NamespacedId name);

	@NotNull
	DictionaryItem getOrFail(@NotNull ItemStack item);

	@NotNull
	List<DictionaryItem> getById(@NotNull String id);

	@NotNull
	DictionaryItem registerItem(@NotNull NamespacedId name, @NotNull ItemStack item, @NotNull ItemBehavior ...behaviors);

	boolean hasItem(@NotNull NamespacedId name);
	
}
