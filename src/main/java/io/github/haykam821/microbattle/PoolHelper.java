package io.github.haykam821.microbattle;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public final class PoolHelper {
	private PoolHelper() {
		return;
	}

	public static <T> WeightedList<T> filter(WeightedList<T> pool, Predicate<T> predicate) {
		List<Weighted<T>> entries = pool.unwrap()
			.stream()
			.filter(entry -> {
				return predicate.test(entry.value());
			})
			.toList();

		return WeightedList.of(entries);
	}
}
