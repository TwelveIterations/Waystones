package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

public class LiteralNameGenerator implements NameGenerator {

    private final String literal;

    public LiteralNameGenerator(String literal) {
        this.literal = literal;
    }

    @Override
    public Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        return Optional.of(Component.literal(literal));
    }
}
