package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TemplateNameGenerator implements NameGenerator {
    private final String template;
    private final Map<String, NameGenerator> placeholders = new HashMap<>();

    public TemplateNameGenerator(String template) {
        this.template = template;
    }

    public TemplateNameGenerator with(String key, NameGenerator nameGenerator) {
        placeholders.put(key, nameGenerator);
        return this;
    }

    public TemplateNameGenerator with(String key, String value) {
        return with(key, new LiteralNameGenerator(value));
    }

    @Override
    public Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        var result = template;
        for (final var entry : placeholders.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue().generateName(level, waystone, rand).orElse(null);
            if (value != null) {
                result = result.replaceAll("\\{" + key + "}", value.getString());
            }
        }
        return Optional.of(Component.literal(result));
    }
}
