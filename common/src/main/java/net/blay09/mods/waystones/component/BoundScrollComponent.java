package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.UUID;

public record BoundScrollComponent(UUID waystoneId, Component waystoneName) {
    public static final Codec<BoundScrollComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(BoundScrollComponent::waystoneId),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(BoundScrollComponent::waystoneName)
    ).apply(instance, BoundScrollComponent::new));
}
