package io.github.sunshinewzy.shining.api.namespace;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * The {@link #name} of namespace may only contain lowercase alphanumeric characters,
 * underscores, and hyphens.
 */
public class Namespace {

	@JsonValue
	private final String name;

	private Namespace(@NotNull String name) {
		Preconditions.checkArgument(VALID_NAMESPACE.matcher(name).matches(), "Invalid namespace. Must be [a-z0-9_-]: %s", name);
		this.name = name;
	}

	@NotNull
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Namespace namespace = (Namespace) o;
		return Objects.equals(name, namespace.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}


	public static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9_-]+");
	private static final ConcurrentMap<String, Namespace> cache = new ConcurrentHashMap<>();

	@NotNull
	@JsonCreator
	public static Namespace get(@NotNull String name) {
		Namespace namespace = cache.get(name);
		if (namespace != null) return namespace;

		Namespace newNamespace = new Namespace(name);
		cache.put(name, newNamespace);
		return newNamespace;
	}

}