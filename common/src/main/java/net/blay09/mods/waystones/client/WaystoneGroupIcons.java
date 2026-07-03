package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneGroupIcons {

    public static ResourceLocation getIcon(WaystoneGroup group) {
        if (WaystoneGroups.DIMENSION_ICON.equals(group.icon()) && group.identifier().getPath().startsWith("dimension/")) {
            final var specificIcon = id("groups/" + group.identifier().getPath());
            if (Minecraft.getInstance().getResourceManager().getResource(getSpriteResource(specificIcon)).isPresent()) {
                return specificIcon;
            }
        }

        return group.icon();
    }

    private static ResourceLocation getSpriteResource(ResourceLocation sprite) {
        return sprite.withPath(path -> "textures/gui/sprites/" + path + ".png");
    }
}
