package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

public class BiomeNameGenerator implements NameGenerator {
    @Override
    public Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        final var biome = level.getBiome(waystone.getPos());
        return Optional.of(biome.unwrapKey()
                .map(ResourceKey::identifier)
                .map(Identifier::toString)
                .map(it -> "biome." + it.replace(":", "."))
                .map(Component::translatable)
                .orElseGet(() -> Component.literal("Corrupted Lands")));
    }
}
