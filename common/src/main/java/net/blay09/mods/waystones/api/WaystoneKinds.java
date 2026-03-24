package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneKinds {
    public static final Identifier INVALID = Identifier.fromNamespaceAndPath("waystones", "invalid");
    public static final Identifier WAYSTONE = Identifier.fromNamespaceAndPath("waystones", "waystone");
    public static final Identifier WARP_PLATE = Identifier.fromNamespaceAndPath("waystones", "warp_plate");

    public static final Set<Identifier> SHARESTONES = Collections.synchronizedSet(new HashSet<>());

    static {
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "ruined_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "copper_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "prismarine_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "gold_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "diamond_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "amethyst_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "lapis_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "emerald_sharestone"));
        SHARESTONES.add(Identifier.fromNamespaceAndPath("waystones", "redstone_sharestone"));
    }

    public static boolean isSharestone(Identifier kind) {
        return SHARESTONES.contains(kind);
    }

    @Nullable
    public static Identifier getKind(@Nullable SharestoneType type) {
        return type != null ? type.kind() : null;
    }
}
