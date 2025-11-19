package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WaystoneTypes {
    public static final Identifier WAYSTONE = Identifier.fromNamespaceAndPath("waystones", "waystone");
    public static final Identifier WARP_PLATE = Identifier.fromNamespaceAndPath("waystones", "warp_plate");

    public static final Identifier[] SHARESTONES = new Identifier[]{
            Identifier.fromNamespaceAndPath("waystones", "orange_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "magenta_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "light_blue_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "yellow_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "lime_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "pink_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "gray_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "light_gray_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "cyan_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "purple_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "blue_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "brown_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "green_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "red_sharestone"),
            Identifier.fromNamespaceAndPath("waystones", "black_sharestone")
    };

    public static Optional<Identifier> getSharestone(@Nullable DyeColor color) {
        if (color == null || color == DyeColor.WHITE) {
            return Optional.empty();
        }

        return Optional.of(Identifier.fromNamespaceAndPath("waystones", color.getSerializedName() + "_sharestone"));
    }

    public static boolean isSharestone(Identifier waystoneType) {
        return waystoneType.getPath().endsWith("_sharestone");
    }
}
