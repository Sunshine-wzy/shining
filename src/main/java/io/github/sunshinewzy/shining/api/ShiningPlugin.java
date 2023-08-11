package io.github.sunshinewzy.shining.api;

import io.github.sunshinewzy.shining.api.namespace.Namespace;

public interface ShiningPlugin {

	/**
	 * Name of the plugin.
	 */
	String getName();

	/**
	 * The namespace may only contain lowercase alphanumeric characters, periods,
	 * underscores, and hyphens.
	 */
	default Namespace getNamespace() {
		return Namespace.get(getName().toLowerCase());
	}
	
	default String getPrefix() {
		return getName();
	}
	
}
