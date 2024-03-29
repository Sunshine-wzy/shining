package io.github.sunshinewzy.shining.api.blueprint;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface IBlueprintClass {
	
	@NotNull
	ArrayList<IBlueprintNodeTree> getNodeTrees();
	
	void edit(@NotNull Player player);

	/**
	 * @return A Component of the matching type, otherwise throw an exception.
	 */
	@NotNull
	<T extends IBlueprintComponent> T getComponent(@NotNull Class<T> type);

	/**
	 * @return A Component of the matching type, otherwise null if no Component is found.
	 */
	@Nullable
	<T extends IBlueprintComponent> T getComponentOrNull(@NotNull Class<T> type);

	/**
	 * Add the component to the blueprint.
	 *
	 * @param component An instance of type.
	 * @return component
	 */
	@NotNull
	<T extends IBlueprintComponent> T addComponent(@NotNull Class<T> type, @NotNull T component);

	/**
	 * Add the component to the blueprint. The [type] will be instantiated by reflection.
	 *
	 * @param type The class must have a constructor with one parameter {@link IBlueprintClass}.
	 * @return An instance of type
	 */
	@NotNull
	<T extends IBlueprintComponent> T addComponent(@NotNull Class<T> type);

	/**
	 * Remove the component from the blueprint.
	 */
	@Nullable
	<T extends IBlueprintComponent> T removeComponent(@NotNull Class<T> type);

	/**
	 * Check if the blueprint has the component of type.
	 */
	<T extends IBlueprintComponent> boolean hasComponent(@NotNull Class<T> type);

	/**
	 * Check if type has the lifecycle.
	 */
	<T extends IBlueprintComponent> boolean hasComponentLifecycle(@NotNull Class<T> type, @NotNull BlueprintComponentLifecycle lifecycle);

	/**
	 * Execute the specified lifecycle methods of all components.
	 */
	void doLifecycle(@NotNull BlueprintComponentLifecycle lifecycle);
	
}
