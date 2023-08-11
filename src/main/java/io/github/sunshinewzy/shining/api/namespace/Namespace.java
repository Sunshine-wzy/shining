package io.github.sunshinewzy.shining.api.namespace;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Namespace {
	
	private String name;
	
	
	private Namespace(@NotNull String name) {
		Preconditions.checkArgument(VALID_NAMESPACE.matcher(name).matches(), "Invalid namespace. Must be [a-z0-9_-]: %s", name);
		this.name = name;
	}


	public static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9_-]+");
	
}
