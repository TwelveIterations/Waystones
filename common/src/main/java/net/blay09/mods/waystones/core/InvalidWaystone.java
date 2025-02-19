package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class InvalidWaystone implements IWaystone {

    public static final IWaystone INSTANCE = new InvalidWaystone();

    @Override
    public boolean isValid() {
        return false;
    }

    @Nullable
    @Override
    public UUID getOwnerUid() {
        return null;
    }

    @Override
    public UUID getWaystoneUid() {
        return UUID.randomUUID();
    }

    @Override
    public String getName() {
        return "invalid";
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
    public boolean isGlobal() {
        return false;
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
    public ResourceLocation getWaystoneType() {
        return new ResourceLocation(Waystones.MOD_ID, "invalid");
    }
}
