package io.github.sunshinewzy.shining.api.blueprint;

import io.github.sunshinewzy.shining.api.utils.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The logical unit of blueprints
 */
public interface IBlueprintNodeTree {
	
	/**
	 * Gets the root of the node tree.
	 */
	@Nullable
	IBlueprintNode getRootOrNull();

	/**
	 * Gets the root of the node tree.
	 * Throws an exception when the root does not exist.
	 */
	@NotNull
	default IBlueprintNode getRoot() {
		IBlueprintNode rootOrNull = getRootOrNull();
		Intrinsics.checkNotNull(rootOrNull);
		return rootOrNull;
	}

	/**
	 * Sets the root of the node tree.
	 *
	 * @return The previous predecessor node, or null if it does not exist
	 */
	@Nullable
	IBlueprintNode setRoot(@NotNull IBlueprintNode node);

}