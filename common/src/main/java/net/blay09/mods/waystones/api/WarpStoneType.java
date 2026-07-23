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

public class WarpStoneType implements Comparable<WarpStoneType> {
    private static final Map<Identifier, WarpStoneType> TYPES_BY_IDENTIFIER = new LinkedHashMap<>();
    private static final Comparator<WarpStoneType> COMPARATOR = Comparator.comparing(WarpStoneType::identifier);
    public static final Codec<WarpStoneType> CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        final var type = get(identifier);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown warp stone type: " + identifier);
    }, WarpStoneType::identifier);

    private final Identifier identifier;
    private final Identifier kind;

    public WarpStoneType(Identifier identifier, Identifier kind) {
        this.identifier = identifier;
        this.kind = kind;
    }

    public static synchronized WarpStoneType register(WarpStoneType type) {
        final var existingByIdentifier = TYPES_BY_IDENTIFIER.get(type.identifier);
        if (existingByIdentifier != null) {
            if (!existingByIdentifier.equals(type)) {
                throw new IllegalArgumentException("Duplicate warp stone type identifier: " + type.identifier);
            }
            return existingByIdentifier;
        }

        TYPES_BY_IDENTIFIER.put(type.identifier, type);
        return type;
    }

    public static Stream<WarpStoneType> values() {
        return TYPES_BY_IDENTIFIER.values().stream();
    }

    @Nullable
    public static WarpStoneType get(Identifier identifier) {
        return TYPES_BY_IDENTIFIER.get(identifier);
    }

    public Identifier identifier() {
        return identifier;
    }

    public Identifier kind() {
        return kind;
    }

    @Override
    public int compareTo(@NonNull WarpStoneType o) {
        return COMPARATOR.compare(this, o);
    }
}
