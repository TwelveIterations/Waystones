package net.blay09.mods.waystones.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class WaystoneType implements Comparable<WaystoneType> {
    private static final Map<Identifier, WaystoneType> TYPES_BY_IDENTIFIER = new LinkedHashMap<>();
    private static final Comparator<WaystoneType> COMPARATOR = Comparator.comparing(WaystoneType::identifier);
    public static final Codec<WaystoneType> CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        final var type = get(identifier);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown waystone type: " + identifier);
    }, WaystoneType::identifier);

    private final Identifier identifier;
    private final int runeColor;

    public WaystoneType(Identifier identifier, int runeColor) {
        this.identifier = identifier;
        this.runeColor = runeColor;
    }

    public static synchronized WaystoneType register(WaystoneType type) {
        final var existingByIdentifier = TYPES_BY_IDENTIFIER.get(type.identifier);
        if (existingByIdentifier != null) {
            if (!existingByIdentifier.equals(type)) {
                throw new IllegalArgumentException("Duplicate waystone type identifier: " + type.identifier);
            }
            return existingByIdentifier;
        }

        TYPES_BY_IDENTIFIER.put(type.identifier, type);
        return type;
    }

    public static Stream<WaystoneType> values() {
        return TYPES_BY_IDENTIFIER.values().stream();
    }

    @Nullable
    public static WaystoneType get(Identifier identifier) {
        return TYPES_BY_IDENTIFIER.get(identifier);
    }

    public Identifier identifier() {
        return identifier;
    }

    public int runeColor() {
        return runeColor;
    }

    @Override
    public int compareTo(@NonNull WaystoneType o) {
        return COMPARATOR.compare(this, o);
    }
}
