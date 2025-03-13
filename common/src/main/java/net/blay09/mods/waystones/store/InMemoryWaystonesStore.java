package net.blay09.mods.waystones.store;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.blay09.mods.waystones.api.MutableWaystone;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;

import java.util.*;

public class InMemoryWaystonesStore implements WaystonesStore {

    private final List<Waystone> waystones = new ArrayList<>();
    private final Map<UUID, Waystone> waystonesById = new HashMap<>();
    private final Multimap<ResourceLocation, Waystone> waystonesByType = ArrayListMultimap.create();

    public InMemoryWaystonesStore(List<Waystone> waystones) {
        this.waystones.addAll(waystones);
        for (final var waystone : waystones) {
            waystonesById.put(waystone.getWaystoneUid(), waystone);
            waystonesByType.put(waystone.getWaystoneType(), waystone);
        }
    }

    @Override
    public void addWaystone(Waystone waystone) {
        waystones.add(waystone);
        waystonesById.put(waystone.getWaystoneUid(), waystone);
        waystonesByType.put(waystone.getWaystoneType(), waystone);
    }

    @Override
    public void updateWaystone(Waystone waystone) {
        final var backingWaystone = waystonesById.get(waystone.getWaystoneUid());
        if (backingWaystone instanceof MutableWaystone mutableWaystone) {
            mutableWaystone.setName(waystone.getName());
            mutableWaystone.setVisibility(waystone.getVisibility());
        } else if (!(waystone instanceof WaystoneProxy) && waystone.isValid()) {
            addWaystone(waystone);
        }
    }

    @Override
    public void removeWaystone(Waystone waystone) {
        final var removedWaystone = waystonesById.remove(waystone.getWaystoneUid());
        waystones.remove(removedWaystone);
        waystonesByType.get(waystone.getWaystoneType()).remove(removedWaystone);
    }

    @Override
    public Optional<Waystone> getWaystoneAt(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase blockEntity) {
            return Optional.of(blockEntity.getWaystone());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Waystone> getWaystoneById(UUID waystoneUid) {
        return Optional.ofNullable(waystonesById.get(waystoneUid));
    }

    @Override
    public Optional<Waystone> findWaystoneByName(String name) {
        return waystones.stream().filter(it -> it.getName().getString().equals(name)).findFirst();
    }

    @Override
    public List<Waystone> getWaystones() {
        return waystones;
    }

    @Override
    public Collection<Waystone> getWaystonesByType(ResourceLocation type) {
        return waystonesByType.get(type);
    }

    @Override
    public List<Waystone> getGlobalWaystones() {
        return waystones.stream().filter(it -> it.getVisibility() == WaystoneVisibility.GLOBAL).toList();
    }

}
