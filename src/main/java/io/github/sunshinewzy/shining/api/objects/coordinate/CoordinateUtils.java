package io.github.sunshinewzy.shining.api.objects.coordinate;

import io.github.sunshinewzy.shining.api.utils.Coerce;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("OptionalIsPresent")
public class CoordinateUtils {

	public static int orderWith(int x, int y) {
		return (y - 1) * 9 + (x - 1);
	}
	
	@Nullable
	public static Coordinate3D coordinate3DFromString(@NotNull String source) {
		String[] list = source.split(",");
		if (list.length != 3) return null;

		Optional<Integer> x = Coerce.asInteger(list[0]);
		if (!x.isPresent()) return null;
		Optional<Integer> y = Coerce.asInteger(list[1]);
		if (!y.isPresent()) return null;
		Optional<Integer> z = Coerce.asInteger(list[2]);
		if (!z.isPresent()) return null;
		return new Coordinate3D(x.get(), y.get(), z.get());
	}

	@Nullable
	public static Coordinate2D coordinate2DFromString(@NotNull String source) {
		String[] list = source.split(",");
		if (list.length != 2) return null;

		Optional<Integer> x = Coerce.asInteger(list[0]);
		if (!x.isPresent()) return null;
		Optional<Integer> y = Coerce.asInteger(list[1]);
		if (!y.isPresent()) return null;
		return new Coordinate2D(x.get(), y.get());
	}
	
}
