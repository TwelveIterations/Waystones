package net.blay09.mods.waystones.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PortstoneType implements Comparable<PortstoneType> {
    private static final Map<Identifier, PortstoneType> TYPES_BY_IDENTIFIER = new LinkedHashMap<>();
    private static final Comparator<PortstoneType> COMPARATOR = Comparator.comparing(PortstoneType::identifier);
    public static final Codec<PortstoneType> CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        final var type = get(identifier);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown portstone type: " + identifier);
    }, PortstoneType::identifier);

    private final Identifier identifier;
    private final DyeColor color;
    private final Identifier kind;

    public PortstoneType(Identifier identifier, DyeColor color, Identifier kind) {
        this.identifier = identifier;
        this.color = color;
        this.kind = kind;
    }

    public static synchronized PortstoneType register(PortstoneType type) {
        final var existingByIdentifier = TYPES_BY_IDENTIFIER.get(type.identifier);
        if (existingByIdentifier != null) {
            if (!existingByIdentifier.equals(type)) {
                throw new IllegalArgumentException("Duplicate portstone type identifier: " + type.identifier);
            }
            return existingByIdentifier;
        }

        TYPES_BY_IDENTIFIER.put(type.identifier, type);
        return type;
    }

    public static Stream<PortstoneType> values() {
        return TYPES_BY_IDENTIFIER.values().stream();
    }

    @Nullable
    public static PortstoneType get(Identifier identifier) {
        return TYPES_BY_IDENTIFIER.get(identifier);
    }

    public int textColor() {
        return color.getTextColor();
    }

    public int textureDiffuseColor() {
        return color.getTextureDiffuseColor();
    }

    public Identifier identifier() {
        return identifier;
    }

    public Identifier kind() {
        return kind;
    }

    @Override
    public int compareTo(@NonNull PortstoneType o) {
        return COMPARATOR.compare(this, o);
    }
}
