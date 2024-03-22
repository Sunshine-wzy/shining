package io.github.sunshinewzy.shining.api.blueprint;

import org.jetbrains.annotations.NotNull;

public enum BlueprintComponentLifecycle {
	/**
	 * The component is added to the blueprint.
	 */
	LOAD("onLoad"),

	/**
	 * The component is enabled.
	 */
	ENABLE("onEnable"),

	/**
	 * The blueprint is created.
	 */
	ACTIVATE("onActivate"),

	/**
	 * Every tick.
	 */
	UPDATE("onUpdate"),

	/**
	 * The blueprint is destroyed.
	 */
	DEACTIVATE("onDeactivate"),

	/**
	 * The component is disabled.
	 */
	DISABLE("onDisable"),

	/**
	 * The component is removed from the blueprint.
	 */
	DESTROY("onDestroy");


	@NotNull
	private final String methodName;
	
	BlueprintComponentLifecycle(@NotNull String methodName) {
		this.methodName = methodName;
	}

	@NotNull
	public String getMethodName() {
		return methodName;
	}
}
