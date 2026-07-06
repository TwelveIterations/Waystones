package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneGroupIcons {

    private static final String SPRITE_RESOURCE_PREFIX = "textures/gui/sprites/";
    private static final String GROUP_SPRITE_RESOURCE_PREFIX = SPRITE_RESOURCE_PREFIX + "groups";
    private static final String PNG_SUFFIX = ".png";

    public static ResourceLocation getIcon(WaystoneGroup group) {
        if (WaystoneGroups.DIMENSION_ICON.equals(group.icon()) && group.identifier().getPath().startsWith("dimension/")) {
            final var specificIcon = id("groups/" + group.identifier().getPath());
            if (Minecraft.getInstance().getResourceManager().getResource(getSpriteResource(specificIcon)).isPresent()) {
                return specificIcon;
            }
        }

        return group.icon();
    }

    public static List<ResourceLocation> getPresetIcons() {
        final var resourceManager = Minecraft.getInstance().getResourceManager();
        final var icons = resourceManager
                .listResources(GROUP_SPRITE_RESOURCE_PREFIX, it ->
                        it.getNamespace().equals(Waystones.MOD_ID) && it.getPath().endsWith(PNG_SUFFIX))
                .keySet()
                .stream()
                .map(WaystoneGroupIcons::toSpriteLocation)
                .sorted(Comparator.comparing(ResourceLocation::getPath))
                .toList();

        return icons.isEmpty() ? List.of(WaystoneGroups.FAVORITES_ICON) : icons;
    }

    private static ResourceLocation toSpriteLocation(ResourceLocation resource) {
        final var path = resource.getPath();
        final var spritePath = path.substring(SPRITE_RESOURCE_PREFIX.length(), path.length() - PNG_SUFFIX.length());
        return resource.withPath(spritePath);
    }

    private static ResourceLocation getSpriteResource(ResourceLocation sprite) {
        return sprite.withPath(path -> SPRITE_RESOURCE_PREFIX + path + PNG_SUFFIX);
    }
}
