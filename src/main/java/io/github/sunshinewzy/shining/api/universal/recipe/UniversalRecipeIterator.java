package io.github.sunshinewzy.shining.api.universal.recipe;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UniversalRecipeIterator implements Iterator<List<ItemStack>> {
	
	private final List<UniversalRecipeChoice> recipe;
	private final ArrayList<Iterator<ItemStack>> iterators = new ArrayList<>();
	private final ArrayList<ItemStack> cache = new ArrayList<>();
	
	public UniversalRecipeIterator(@NotNull List<UniversalRecipeChoice> recipe) {
		this.recipe = recipe;
		for (UniversalRecipeChoice choice : recipe) {
			iterators.add(choice.iterator());
		}
	}
	
	public UniversalRecipeIterator(@NotNull UniversalRecipe recipe) {
		this(recipe.getRecipe());
	}

	public List<UniversalRecipeChoice> getRecipe() {
		return recipe;
	}

	@Override
	public boolean hasNext() {
		for (Iterator<ItemStack> iterator : iterators) {
			if (iterator.hasNext())
				return true;
		}
		return false;
	}

	@Override
	public List<ItemStack> next() {
		if (cache.isEmpty()) {
			for (Iterator<ItemStack> iterator : iterators) {
				cache.add(iterator.next());
			}
		} else {
			int i = 0;
			for (Iterator<ItemStack> iterator : iterators) {
				if (iterator.hasNext()) {
					cache.set(i, iterator.next());
				}
				i++;
			}
		}
		return new ArrayList<>(cache);
	}
	
	public void reset() {
		iterators.clear();
		for (UniversalRecipeChoice choice : recipe) {
			iterators.add(choice.iterator());
		}
	}
	
}
