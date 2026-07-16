package io.github.haykam821.microbattle.game.kit;

import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.world.item.ItemStackTemplate;
import xyz.nucleoid.plasmid.api.util.TinyRegistry;

public class KitType<T extends Kit> {
	public static final TinyRegistry<KitType<?>> REGISTRY = TinyRegistry.create();

	private final Function<PlayerEntry, T> creator;
	private final ItemStackTemplate icon;
	private String translationKey;

	public KitType(Function<PlayerEntry, T> creator, ItemStackTemplate icon) {
		this.creator = creator;
		this.icon = icon;
	}
	
	private T create(PlayerEntry entry) {
		return this.creator.apply(entry);
	}

	public Kit create(PlayerEntry entry, KitType<?> layer) {
		T kit = this.create(entry);

		if (layer == null){
			return kit;
		} else {
			return new LayeredKit(kit, layer.create(entry), entry);
		}
	}

	public ItemStack getIcon() {
		return this.icon.create();
	}

	private String getTranslationKey() {
		if (this.translationKey == null) {
			Identifier id = KitType.REGISTRY.getIdentifier(this);
			this.translationKey = "kit." + id.getNamespace() + "." + id.getPath();
		}
		return this.translationKey;
	}

	public Component getName() {
		return Component.translatable(this.getTranslationKey());
	}
}
