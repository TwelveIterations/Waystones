package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WaystoneProxy implements IWaystone, IMutableWaystone {

    private final MinecraftServer server;
    private final UUID waystoneUid;
    private IWaystone backingWaystone;

    public WaystoneProxy(@Nullable MinecraftServer server, UUID waystoneUid) {
        this.server = server;
        this.waystoneUid = waystoneUid;
    }

    @Override
    public boolean isValidInLevel(ServerLevel level) {
        return getBackingWaystone().isValidInLevel(level);
    }

    @Override
    public TeleportDestination resolveDestination(ServerLevel level) {
        return getBackingWaystone().resolveDestination(level);
    }

    @Override
    public boolean isValid() {
        return WaystoneManager.get(server).getWaystoneById(waystoneUid).isPresent();
    }

    public IWaystone getBackingWaystone() {
        if (backingWaystone == null) {
            backingWaystone = WaystoneManager.get(server).getWaystoneById(waystoneUid).orElse(InvalidWaystone.INSTANCE);
        }

        return backingWaystone;
    }

    @Override
    public UUID getOwnerUid() {
        return getBackingWaystone().getOwnerUid();
    }

    @Override
    public UUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public String getName() {
        return getBackingWaystone().getName();
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return getBackingWaystone().getDimension();
    }

    @Override
    public boolean wasGenerated() {
        return getBackingWaystone().wasGenerated();
    }

    @Override
    public WaystoneOrigin getOrigin() {
        return getBackingWaystone().getOrigin();
    }

    @Override
    public boolean isGlobal() {
        return getBackingWaystone().isGlobal();
    }

    @Override
    public boolean isOwner(Player player) {
        return getBackingWaystone().isOwner(player);
    }

    @Override
    public BlockPos getPos() {
        return getBackingWaystone().getPos();
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return getBackingWaystone().getWaystoneType();
    }

    @Override
    public void setName(String name) {
        IWaystone backingWaystone = getBackingWaystone();
        if (backingWaystone instanceof IMutableWaystone) {
            ((IMutableWaystone) backingWaystone).setName(name);
        }
    }

    @Override
    public void setGlobal(boolean global) {
        IWaystone backingWaystone = getBackingWaystone();
        if (backingWaystone instanceof IMutableWaystone) {
            ((IMutableWaystone) backingWaystone).setGlobal(global);
        }
    }

    @Override
    public void setDimension(ResourceKey<Level> dimension) {
        IWaystone backingWaystone = getBackingWaystone();
        if (backingWaystone instanceof IMutableWaystone) {
            ((IMutableWaystone) backingWaystone).setDimension(dimension);
        }
    }

    @Override
    public void setPos(BlockPos pos) {
        IWaystone backingWaystone = getBackingWaystone();
        if (backingWaystone instanceof IMutableWaystone) {
            ((IMutableWaystone) backingWaystone).setPos(pos);
        }
    }

    @Override
    public void setOwnerUid(UUID ownerUid) {
        IWaystone backingWaystone = getBackingWaystone();
        if (backingWaystone instanceof IMutableWaystone) {
            ((IMutableWaystone) backingWaystone).setOwnerUid(ownerUid);
        }
    }
}
