package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WaystoneTypes {
    public static final Identifier WAYSTONE = Identifier.fromNamespaceAndPath("waystones", "waystone");
    public static final Identifier WARP_PLATE = Identifier.fromNamespaceAndPath("waystones", "warp_plate");

    public static final Identifier[] SHARESTONES = new Identifier[]{
            Identifier.fromNamespaceAndPath("waystones", "copper_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "prismarine_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "gold_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "diamond_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "amethyst_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "lapis_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "emerald_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "redstone_sharestone")
    };

    public static Optional<Identifier> getSharestone(@Nullable SharestoneType type) {
        if (type == null) {
            return Optional.empty();
        }

        return Optional.of(Identifier.fromNamespaceAndPath("waystones", type.getSerializedName() + "_sharestone"));
    }

    public static boolean isSharestone(Identifier waystoneType) {
        return waystoneType.getPath().endsWith("_sharestone");
    }
}
