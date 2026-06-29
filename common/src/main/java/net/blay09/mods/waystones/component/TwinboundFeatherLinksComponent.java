package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public record TwinboundFeatherLinksComponent(List<UUID> links) {

    public static final TwinboundFeatherLinksComponent EMPTY = new TwinboundFeatherLinksComponent(List.of());
    public static final Codec<TwinboundFeatherLinksComponent> CODEC = UUIDUtil.CODEC.listOf().xmap(TwinboundFeatherLinksComponent::new, TwinboundFeatherLinksComponent::links);

    public TwinboundFeatherLinksComponent {
        links = List.copyOf(new LinkedHashSet<>(links));
    }

    public TwinboundFeatherLinksComponent with(UUID link) {
        if (links.contains(link)) {
            return this;
        }

        final var newLinks = new LinkedHashSet<>(links);
        newLinks.add(link);
        return new TwinboundFeatherLinksComponent(List.copyOf(newLinks));
    }
}
