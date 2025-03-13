package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record EventfulWaystonesStore(WaystonesStore delegate) implements WaystonesStore {
    @Override
    public void addWaystone(Waystone waystone) {
        delegate.addWaystone(waystone);
    }

    @Override
    public void updateWaystone(Waystone waystone) {
        delegate.updateWaystone(waystone);
        Balm.getEvents().fireEvent(new WaystoneUpdatedEvent(waystone));
    }

    @Override
    public void removeWaystone(Waystone waystone) {
        delegate.removeWaystone(waystone);
        Balm.getEvents().fireEvent(new WaystoneRemovedEvent(waystone));
    }

    @Override
    public Optional<Waystone> getWaystoneAt(BlockGetter world, BlockPos pos) {
        return delegate.getWaystoneAt(world, pos);
    }

    @Override
    public Optional<Waystone> getWaystoneById(UUID waystoneUid) {
        return delegate.getWaystoneById(waystoneUid);
    }

    @Override
    public Optional<Waystone> findWaystoneByName(String name) {
        return delegate.findWaystoneByName(name);
    }

    @Override
    public List<Waystone> getWaystones() {
        return delegate.getWaystones();
    }

    @Override
    public Collection<Waystone> getWaystonesByType(ResourceLocation type) {
        return delegate.getWaystonesByType(type);
    }

    @Override
    public List<Waystone> getGlobalWaystones() {
        return delegate.getGlobalWaystones();
    }
}
