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

public class SharestoneType implements Comparable<SharestoneType> {
    private static final Map<Identifier, SharestoneType> TYPES_BY_IDENTIFIER = new LinkedHashMap<>();
    private static final Comparator<SharestoneType> COMPARATOR = Comparator.comparing(SharestoneType::identifier);
    public static final Codec<SharestoneType> CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        final var type = get(identifier);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown sharestone type: " + identifier);
    }, SharestoneType::identifier);

    private final Identifier identifier;
    private final DyeColor color;

    private Identifier kind;

    public SharestoneType(Identifier identifier, DyeColor color) {
        this.identifier = identifier;
        this.kind = identifier;
        this.color = color;
    }

    public SharestoneType withKind(Identifier kind) {
        this.kind = kind;
        return this;
    }

    public static synchronized SharestoneType register(SharestoneType type) {
        final var existingByIdentifier = TYPES_BY_IDENTIFIER.get(type.identifier);
        if (existingByIdentifier != null) {
            if (!existingByIdentifier.equals(type)) {
                throw new IllegalArgumentException("Duplicate sharestone type identifier: " + type.identifier);
            }
            return existingByIdentifier;
        }

        TYPES_BY_IDENTIFIER.put(type.identifier, type);
        return type;
    }

    public static Stream<SharestoneType> values() {
        return TYPES_BY_IDENTIFIER.values().stream();
    }

    @Nullable
    public static SharestoneType get(Identifier identifier) {
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
    public int compareTo(@NonNull SharestoneType o) {
        return COMPARATOR.compare(this, o);
    }
}
