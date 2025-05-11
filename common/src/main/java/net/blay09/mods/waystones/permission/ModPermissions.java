package net.blay09.mods.waystones.permission;

import net.blay09.mods.balm.api.permission.BalmPermissions;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;

public class ModPermissions {
    public static final ResourceLocation EDIT_ALL = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "edit_all");

    public static void initialize(BalmPermissions permissions) {
        permissions.registerPermission(EDIT_ALL, (context) -> context.getPlayer().map(it -> it.getAbilities().instabuild).orElse(false));
    }
}
