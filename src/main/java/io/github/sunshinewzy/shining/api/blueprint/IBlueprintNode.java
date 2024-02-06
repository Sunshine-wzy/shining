package io.github.sunshinewzy.shining.api.blueprint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.sunshinewzy.shining.api.utils.Intrinsics;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The basic unit of blueprints
 */
public interface IBlueprintNode {

	/**
	 * Executed when the node is executed.
	 */
	void onExecute();

	/**
	 * Executed when the node is edited.
	 */
	void onEdit();

	/**
	 * Gets the icon of the node.
	 */
	@NotNull
	ItemStack getIcon();

	/**
	 * Gets the amount of successor nodes.
	 */
	int getSuccessorAmount();

	/**
	 * Gets all successor nodes.
	 * The size of the array is {@code getSuccessorAmount}.
	 */
	@NotNull
	IBlueprintNode[] getSuccessors();

	/**
	 * Gets the first successor node.
	 */
	@Nullable
	@JsonIgnore
	default IBlueprintNode getSuccessorOrNull() {
		IBlueprintNode[] successors = getSuccessors();
		return successors.length == 0 ? null : successors[0];
	}

	/**
	 * Gets the first successor node.
	 * Throws an exception when there is no successor node.
	 */
	@NotNull
	@JsonIgnore
	default IBlueprintNode getSuccessor() {
		return getSuccessors()[0];
	}

	/**
	 * Gets the predecessor node.
	 */
	@Nullable
	@JsonIgnore
	IBlueprintNode getPredecessorOrNull();

	/**
	 * Gets the predecessor node.
	 * Throws an exception when there is no predecessor node.
	 */
	@NotNull
	@JsonIgnore
	default IBlueprintNode getPredecessor() {
		IBlueprintNode predecessorOrNull = getPredecessorOrNull();
		Intrinsics.checkNotNull(predecessorOrNull);
		return predecessorOrNull;
	}

	/**
	 * Sets the predecessor node.
	 *
	 * @return The previous predecessor node, or null if it does not exist
	 */
	@Nullable
	IBlueprintNode setPredecessor(@NotNull IBlueprintNode node);

	/**
	 * Gets the width of the node.
	 */
	@JsonIgnore
	default int getWidth() {
		IBlueprintNode[] successors = getSuccessors();
		if (successors.length == 0) return 1;
		
		int sum = 0;
		for (IBlueprintNode successor : successors) {
			sum += successor.getWidth();
		}
		return sum;
	}
	
}
