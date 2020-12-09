package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

public class KitType<T extends Kit> {
	public static final TinyRegistry<KitType<?>> REGISTRY = TinyRegistry.newStable();

	private final Function<PlayerEntry, T> creator;
	private String translationKey;

	public KitType(Function<PlayerEntry, T> creator) {
		this.creator = creator;
	}
	
	public T create(PlayerEntry entry) {
		return this.creator.apply(entry);
	}

	private String getTranslationKey() {
		if (this.translationKey == null) {
			Identifier id = KitType.REGISTRY.getIdentifier(this);
			this.translationKey = "kit." + id.getNamespace() + "." + id.getPath();
		}
		return this.translationKey;
	}

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}
}
