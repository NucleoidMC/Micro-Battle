package io.github.haykam821.microbattle.game.kit;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.EitherCodec;

import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.util.TinyRegistry;

public class KitPreset {
	public static final TinyRegistry<List<KitType<?>>> REGISTRY = TinyRegistry.create();
	public static final Codec<List<KitType<?>>> CODEC = Codec.STRING.flatXmap(string -> {
		Identifier id = Identifier.tryParse(string);
		if (id == null) return DataResult.error(() -> "Invalid kit preset ID: '" + string + "'");

		List<KitType<?>> preset = KitPreset.REGISTRY.get(id);
		if (preset == null) return DataResult.error(() -> "Unknown kit preset: '" + id + "'");

		return DataResult.success(preset);
	}, list -> {
		Identifier id = KitPreset.REGISTRY.getIdentifier(list);
		if (id == null) return DataResult.error(() -> "Unknown kit preset: " + list);
		
		return DataResult.success(id.toString());
	});
	public static final EitherCodec<List<KitType<?>>, List<KitType<?>>> EITHER_CODEC = new EitherCodec<>(KitPreset.CODEC, KitType.REGISTRY.listOf());
}
