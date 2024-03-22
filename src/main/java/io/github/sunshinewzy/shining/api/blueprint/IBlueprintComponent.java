package io.github.sunshinewzy.shining.api.blueprint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBlueprintComponent {

	/**
	 * The blueprint class this component is attached to. A component is always attached to a blueprint.
	 */
	@NotNull
	IBlueprintClass getBlueprint();

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
	 * Executed when the component is added to the blueprint.
	 */
	default void onLoad() {}

	/**
	 * Executed when the component is enabled.
	 */
	default void onEnable() {}

	/**
	 * Executed when the blueprint is created.
	 */
	default void onActivate() {}

	/**
	 * Executed every tick.
	 */
	default void onUpdate() {}

	/**
	 * Executed when the blueprint is destroyed.
	 */
	default void onDeactivate() {}

	/**
	 * Executed when the component is disabled.
	 */
	default void onDisable() {}

	/**
	 * Executed when the component is removed from the blueprint.
	 */
	default void onDestroy() {}
	
}
