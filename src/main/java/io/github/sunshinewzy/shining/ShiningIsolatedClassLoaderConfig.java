package io.github.sunshinewzy.shining;

import taboolib.common.classloader.IsolatedClassLoaderConfig;

import java.util.HashSet;
import java.util.Set;

public class ShiningIsolatedClassLoaderConfig implements IsolatedClassLoaderConfig {

	@Override
	public Set<String> excludedPackages() {
		HashSet<String> packages = new HashSet<>();
		packages.add("com.fasterxml.jackson.annotation.");
		packages.add("io.github.sunshinewzy.shining.api.namespace.");
		packages.add("io.github.sunshinewzy.shining.api.dictionary.");
		return packages;
	}
	
}
