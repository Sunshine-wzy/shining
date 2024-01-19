package io.github.sunshinewzy.shining.api.machine.component;

import io.github.sunshinewzy.shining.api.machine.IMachineContext;
import io.github.sunshinewzy.shining.api.machine.IMachineInteractContext;
import io.github.sunshinewzy.shining.api.machine.IMachineRunContext;
import org.jetbrains.annotations.NotNull;

public enum MachineComponentLifecycle {
	/**
	 * The component is added to the machine.
	 */
	LOAD("onLoad"),

	/**
	 * The component is enabled.
	 */
	ENABLE("onEnable"),

	/**
	 * The machine is created.
	 */
	ACTIVATE("onActivate", IMachineContext.class),

	/**
	 * Every tick.
	 */
	UPDATE("onUpdate", IMachineContext.class),

	/**
	 * An interactive block of the machine is interacted with by a player.
	 */
	INTERACT("onInteract", IMachineInteractContext.class),

	/**
	 * The machine is triggered to run.
	 */
	RUN("onRun", IMachineRunContext.class),
	
	/**
	 * The machine is destroyed.
	 */
	DEACTIVATE("onDeactivate", IMachineContext.class),

	/**
	 * The component is disabled.
	 */
	DISABLE("onDisable"),

	/**
	 * The component is removed from the machine.
	 */
	DESTROY("onDestroy");
	
	
	@NotNull
	private final String methodName;
	@NotNull
	private final Class<?>[] parameterTypes;
	
	MachineComponentLifecycle(@NotNull String methodName, @NotNull Class<?>... parameterTypes) {
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
	}
	
	MachineComponentLifecycle(@NotNull String methodName) {
		this(methodName, new Class[]{});
	}

	@NotNull
	public String getMethodName() {
		return methodName;
	}

	@NotNull
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
}
