package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class WaystoneDelegate implements Waystone {

    protected final Waystone delegate;

    public WaystoneDelegate(Waystone delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    public Waystone getDelegate() {
        return delegate;
    }

    @Override
    public UUID getWaystoneUid() {
        return delegate.getWaystoneUid();
    }

    @Override
    public Component getName() {
        return delegate.getName();
    }

    @Override
    public Component getEffectiveName() {
        return delegate.getEffectiveName();
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return delegate.getDimension();
    }

    @Override
    public boolean wasGenerated() {
        return delegate.wasGenerated();
    }

    @Override
    public WaystoneOrigin getOrigin() {
        return delegate.getOrigin();
    }

    @Override
    public boolean isOwner(Player player) {
        return delegate.isOwner(player);
    }

    @Override
    public BlockPos getPos() {
        return delegate.getPos();
    }

    @Override
    public boolean isValid() {
        return delegate.isValid();
    }

    @Override
    public Optional<UUID> getOwnerUid() {
        return delegate.getOwnerUid();
    }

    @Override
    public Optional<String> getOwnerUsername() {
        return delegate.getOwnerUsername();
    }

    @Override
    public Identifier getWaystoneKind() {
        return delegate.getWaystoneKind();
    }

    @Override
    public boolean hasName() {
        return delegate.hasName();
    }

    @Override
    public boolean wasSeen() {
        return Waystone.super.wasSeen();
    }

    @Override
    public boolean hasOwner() {
        return delegate.hasOwner();
    }

    @Override
    public boolean isValidInLevel(ServerLevel level) {
        return delegate.isValidInLevel(level);
    }

    @Override
    public Optional<TeleportDestination> resolveDestination(ServerLevel level) {
        return delegate.resolveDestination(level);
    }

    @Override
    public boolean isTransient() {
        return delegate.isTransient();
    }

    @Override
    public WaystoneVisibility getVisibility() {
        return delegate.getVisibility();
    }

    @Override
    public Set<Identifier> getWaystoneGroups() {
        return delegate.getWaystoneGroups();
    }
}
