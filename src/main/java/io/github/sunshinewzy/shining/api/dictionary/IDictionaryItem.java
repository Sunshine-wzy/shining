package io.github.sunshinewzy.shining.api.dictionary;

import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior;
import io.github.sunshinewzy.shining.api.namespace.NamespacedId;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDictionaryItem {
	
	@NotNull
	NamespacedId getName();
	
	@NotNull
	ItemStack getItemStack();
	
	@NotNull
	List<ItemBehavior> getBehaviors();
	
	boolean hasName();
	
}
