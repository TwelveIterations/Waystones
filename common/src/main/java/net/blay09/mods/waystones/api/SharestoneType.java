package net.blay09.mods.waystones.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SharestoneType implements Comparable<SharestoneType> {
    private static final Map<Identifier, SharestoneType> TYPES_BY_IDENTIFIER = new LinkedHashMap<>();
    private static final Comparator<SharestoneType> COMPARATOR = Comparator.comparing(SharestoneType::identifier);
    public static final Codec<SharestoneType> CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        final var type = get(identifier);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown sharestone type: " + identifier);
    }, SharestoneType::identifier);

    private final Identifier identifier;
    private final Identifier kind;
    private final DyeColor color;
    private final Supplier<Block> blockSupplier;

    public SharestoneType(Identifier identifier, DyeColor color, Supplier<Block> blockSupplier) {
        this(identifier, identifier, color, blockSupplier);
    }

    public SharestoneType(Identifier identifier, Identifier kind, DyeColor color, Supplier<Block> blockSupplier) {
        this.identifier = identifier;
        this.kind = kind;
        this.color = color;
        this.blockSupplier = blockSupplier;
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

    public DyeColor color() {
        return color;
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

    public Block block() {
        return blockSupplier.get();
    }

    @Override
    public int compareTo(SharestoneType o) {
        return COMPARATOR.compare(this, o);
    }
}
