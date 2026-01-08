package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RestrictedWaystone implements Waystone, WaystoneDistanceProvider {

    private final ResourceLocation waystoneType;
    private final UUID waystoneUid;
    private final WaystoneOrigin origin;
    private final ResourceKey<Level> dimension;
    private final Component name;
    private final WaystoneVisibility visibility;
    private final int distance;

    public RestrictedWaystone(ResourceLocation waystoneType, UUID waystoneUid, WaystoneOrigin origin, ResourceKey<Level> dimension, Component name,
                              WaystoneVisibility visibility, int distance) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.origin = origin;
        this.dimension = dimension;
        this.name = name;
        this.visibility = visibility;
        this.distance = distance;
    }

    @Override
    public UUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public WaystoneOrigin getOrigin() {
        return origin;
    }

    @Override
    public boolean isOwner(Player player) {
        return true;
    }

    @Override
    public BlockPos getPos() {
        return BlockPos.ZERO;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    @Nullable
    public UUID getOwnerUid() {
        return null;
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public WaystoneVisibility getVisibility() {
        return visibility;
    }

    @Override
    public boolean hasDistance() {
        return distance >= 0;
    }

    @Override
    public int getDistance() {
        return distance;
    }
}
