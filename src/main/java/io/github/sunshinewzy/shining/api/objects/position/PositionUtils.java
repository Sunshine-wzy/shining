package io.github.sunshinewzy.shining.api.objects.position;

import io.github.sunshinewzy.shining.api.utils.Coerce;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PositionUtils {
	
	@Nullable
	public static Position3D position3DFromString(@NotNull String source) {
		String[] posAndWorld = source.split(";");
		if (posAndWorld.length != 2) return null;

		String[] pos = posAndWorld[0].split(",");
		if (pos.length != 3) return null;

		Optional<Integer> x = Coerce.asInteger(pos[0]);
		if (!x.isPresent()) return null;
		Optional<Integer> y = Coerce.asInteger(pos[1]);
		if (!y.isPresent()) return null;
		Optional<Integer> z = Coerce.asInteger(pos[2]);
		if (!z.isPresent()) return null;

		String world = posAndWorld[1];
		if (world.isEmpty())
			return new Position3D(x.get(), y.get(), z.get());

		return new Position3D(x.get(), y.get(), z.get(), world);
	}
	
}
