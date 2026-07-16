package io.github.haykam821.microbattle;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

public final class PoolHelper {
	private PoolHelper() {
		return;
	}

	public static <T> Pool<T> filter(Pool<T> pool, Predicate<T> predicate) {
		List<Weighted<T>> entries = pool.getEntries()
			.stream()
			.filter(entry -> {
				return predicate.test(entry.value());
			})
			.toList();

		return Pool.of(entries);
	}
}
