package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class InvalidWaystone implements Waystone {

    public static final Waystone INSTANCE = new InvalidWaystone();

    @Override
    public boolean isValid() {
        return false;
    }

    @Nullable
    @Override
    public Optional<UUID> getOwnerUid() {
        return Optional.empty();
    }

    @Override
    public UUID getWaystoneUid() {
        return UUID.randomUUID();
    }

    @Override
    public Component getName() {
        return Component.literal("invalid");
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return Level.OVERWORLD;
    }

    @Override
    public WaystoneOrigin getOrigin() {
        return WaystoneOrigin.UNKNOWN;
    }

    @Override
    public WaystoneVisibility getVisibility() {
        return WaystoneVisibility.ACTIVATION;
    }

    @Override
    public boolean isOwner(Player player) {
        return false;
    }

    @Override
    public BlockPos getPos() {
        return BlockPos.ZERO;
    }

    @Override
    public Identifier getWaystoneKind() {
        return WaystoneKinds.INVALID;
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public boolean hasName() {
        return false;
    }

    @Override
    public boolean wasSeen() {
        return false;
    }

    @Override
    public boolean hasOwner() {
        return false;
    }

    @Override
    public boolean wasGenerated() {
        return false;
    }
}
