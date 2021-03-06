package com.aranaira.arcanearchives.util;

import com.aranaira.arcanearchives.types.IteRef;
import com.aranaira.arcanearchives.types.iterators.TileListIterable;
import com.aranaira.arcanearchives.types.lists.ITileList;
import com.google.common.collect.Iterators;

import java.util.Objects;
import java.util.function.Predicate;

public class TileUtils {
	public static TileListIterable filterValid (ITileList tiles) {
		return new TileListIterable(Iterators.filter(tiles.iterator(), Objects::nonNull));
	}

	public static TileListIterable filterAssignableClass (ITileList tiles, Class<?> clazz) {
		return new TileListIterable(Iterators.filter(tiles.iterator(), (f) -> f != null && clazz.isAssignableFrom(f.clazz)));
	}

	public static TileListIterable filter (ITileList tiles, Predicate<IteRef> predicate) {
		return new TileListIterable(Iterators.filter(tiles.iterator(), predicate::test));
	}
}
