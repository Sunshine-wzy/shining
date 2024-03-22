package io.github.sunshinewzy.shining.api.machine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMachineComponent {

	/**
	 * The machine this component is attached to. A component is always attached to a machine.
	 */
	@NotNull IMachine getMachine();

	/**
	 * @return A Component of the matching type, otherwise throw an exception.
	 */
	@NotNull
	default <T extends IMachineComponent> T getComponent(@NotNull Class<T> type) {
		return getMachine().getComponent(type);
	}

	/**
	 * @return A Component of the matching type, otherwise null if no Component is found.
	 */
	@Nullable
	default <T extends IMachineComponent> T getComponentOrNull(@NotNull Class<T> type) {
		return getMachine().getComponentOrNull(type);
	}

	/**
	 * Add the component to the machine.
	 *
	 * @param component An instance of type.
	 * @return component
	 */
	@NotNull
	default <T extends IMachineComponent> T addComponent(@NotNull Class<T> type, @NotNull T component) {
		return getMachine().addComponent(type, component);
	}

	/**
	 * Add the component to the machine. The type will be instantiated by reflection.
	 *
	 * @param type The class must have a constructor with one parameter {@link IMachine}.
	 * @return An instance of type
	 */
	@NotNull
	default <T extends IMachineComponent> T addComponent(@NotNull Class<T> type) {
		return getMachine().addComponent(type);
	}

	/**
	 * Remove the component from the machine.
	 */
	default <T extends IMachineComponent> T removeComponent(@NotNull Class<T> type) {
		return getMachine().removeComponent(type);
	}

	/**
	 * Check if the machine has the component of type.
	 */
	default <T extends IMachineComponent> boolean hasComponent(@NotNull Class<T> type) {
		return getMachine().hasComponent(type);
	}

	/**
	 * Check if type has the lifecycle.
	 */
	default <T extends IMachineComponent> boolean hasComponentLifecycle(@NotNull Class<T> type, @NotNull MachineComponentLifecycle lifecycle) {
		return getMachine().hasComponentLifecycle(type, lifecycle);
	}

	/**
	 * Executed when the component is added to the machine.
	 */
	default void onLoad() {}

	/**
	 * Executed when the component is enabled.
	 */
	default void onEnable() {}

	/**
	 * Executed when the machine is created.
	 */
	default void onActivate(@NotNull IMachineContext context) {}

	/**
	 * Executed every tick.
	 */
	default void onUpdate(@NotNull IMachineContext context) {}

	/**
	 * Executed when the machine is destroyed.
	 */
	default void onDeactivate(@NotNull IMachineContext context) {}

	/**
	 * Executed when the component is disabled.
	 */
	default void onDisable() {}

	/**
	 * Executed when the component is removed from the machine.
	 */
	default void onDestroy() {}
	
}
