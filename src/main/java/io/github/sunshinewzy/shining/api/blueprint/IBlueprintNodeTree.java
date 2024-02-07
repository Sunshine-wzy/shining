package io.github.sunshinewzy.shining.api.blueprint;

import org.jetbrains.annotations.NotNull;

/**
 * The logical unit of blueprints
 */
public interface IBlueprintNodeTree {
	
	/**
	 * Gets the root of the node tree.
	 */
	@NotNull
	IBlueprintNode getRoot();

	/**
	 * Sets the root of the node tree.
	 *
	 * @return The previous root node
	 */
	@NotNull
	IBlueprintNode setRoot(@NotNull IBlueprintNode node);

}