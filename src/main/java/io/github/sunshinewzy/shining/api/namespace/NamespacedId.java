package io.github.sunshinewzy.shining.api.namespace;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import io.github.sunshinewzy.shining.api.ShiningPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represent a String based id which consists of two components - a namespace
 * and an id.
 * <p>
 * Namespace may only contain lowercase alphanumeric characters,
 * underscores, and hyphens.
 * <p>
 * ID may only contain lowercase alphanumeric characters,
 * underscores, hyphens, and forward slashes.
 */
public class NamespacedId {
	
	private final Namespace namespace;
	private final String id;

	/**
	 * Create an id in a specific namespace.
	 * 
	 * @param namespace namespace
	 * @param id id
	 */
	public NamespacedId(@NotNull Namespace namespace, @NotNull String id) {
		Preconditions.checkArgument(VALID_ID.matcher(id).matches(), "Invalid id. Must be [a-z0-9/_-]: %s", namespace);
		
		this.namespace = namespace;
		this.id = id;

		String string = toString();
		Preconditions.checkArgument(string.length() < 256, "NamespacedId must be less than 256 characters: %s", string);
	}

	/**
	 * Create an id in the plugin's namespace.
	 * <p>
	 * Namespace may only contain lowercase alphanumeric characters,
	 * underscores, and hyphens.
	 * <p>
	 * ID may only contain lowercase alphanumeric characters,
	 * underscores, hyphens, and forward slashes.
	 *
	 * @param plugin the plugin to use for the namespace
	 * @param id the id to create
	 */
	public NamespacedId(@NotNull ShiningPlugin plugin, @NotNull String id) {
		this(plugin.getNamespace(), id);
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NamespacedId that = (NamespacedId) o;
		return Objects.equals(namespace, that.namespace) && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, id);
	}

	@Override
	@JsonValue
	public String toString() {
		return namespace.getName() + ":" + id;
	}


	private static final Namespace SHINING = Namespace.get("shining");
	
	public static final Pattern VALID_ID = Pattern.compile("[a-z0-9/_-]+");
	public static final NamespacedId NULL = shining("null");

	/**
	 * Get an id in the shining namespace.
	 *
	 * @param id the id to use
	 * @return new id in the shining namespace
	 */
	@NotNull
	public static NamespacedId shining(String id) {
		assert SHINING != null;
		return new NamespacedId(SHINING, id);
	}

	/**
	 * Get a NamespacedId from the supplied string with a default namespace if
	 * a namespace is not defined. This is a utility method meant to fetch a
	 * NamespacedId from user input. Please note that casing does matter and
	 * any instance of uppercase characters will be considered invalid. The
	 * input contract is as follows:
	 * <pre>
	 * fromString("foo", plugin) -{@literal >} "plugin:foo"
	 * fromString("foo:bar", plugin) -{@literal >} "foo:bar"
	 * fromString(":foo", null) -{@literal >} "shining:foo"
	 * fromString("foo", null) -{@literal >} "shining:foo"
	 * fromString("Foo", plugin) -{@literal >} null
	 * fromString(":Foo", plugin) -{@literal >} null
	 * fromString("foo:bar:bazz", plugin) -{@literal >} null
	 * fromString("", plugin) -{@literal >} null
	 * </pre>
	 *
	 * @param string the string to convert to a NamespacedId
	 * @param defaultNamespace the default namespace to use if none was
	 * supplied. If null, the `shining` namespace will be used
	 *
	 * @return the created NamespacedId. null if invalid id
	 * @see #fromString(String)
	 */
	@Nullable
	public static NamespacedId fromString(@NotNull String string, @Nullable ShiningPlugin defaultNamespace) {
		Preconditions.checkArgument(!string.isEmpty(), "Input string must not be empty");

		String[] components = string.split(":", 3);
		if (components.length > 2) {
			return null;
		}

		String id = (components.length == 2) ? components[1] : "";
		if (components.length == 1) {
			String value = components[0];
			if (value.isEmpty() || !VALID_ID.matcher(value).matches()) {
				return null;
			}

			return (defaultNamespace != null) ? new NamespacedId(defaultNamespace, value) : shining(value);
		} else if (components.length == 2 && !VALID_ID.matcher(id).matches()) {
			return null;
		}

		String namespace = components[0];
		if (namespace.isEmpty()) {
			return (defaultNamespace != null) ? new NamespacedId(defaultNamespace, id) : shining(id);
		}

		if (!Namespace.VALID_NAMESPACE.matcher(namespace).matches()) {
			return null;
		}

		return new NamespacedId(Namespace.get(namespace), id);
	}

	/**
	 * Get a NamespacedId from the supplied string.
	 * <p>
	 * The default namespace will be shining.
	 *
	 * @param id the id to convert to a NamespacedId
	 * @return the created NamespacedId. null if invalid
	 * @see #fromString(String, ShiningPlugin)
	 */
	@Nullable
	@JsonCreator
	public static NamespacedId fromString(@NotNull String id) {
		return fromString(id, null);
	}
	
}
