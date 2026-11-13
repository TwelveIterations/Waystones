package net.blay09.mods.waystones.store;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.blay09.mods.waystones.api.MutableWaystone;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;

import java.util.*;

public class InMemoryWaystonesStore implements WaystonesStore {

    private final List<Waystone> waystones = Collections.synchronizedList(new ArrayList<>());
    private final Map<UUID, Waystone> waystonesById = Collections.synchronizedMap(new HashMap<>());
    private final BiMap<UUID, GlobalPos> positionByWaystoneId = HashBiMap.create();
    private final Multimap<Identifier, Waystone> waystonesByType = Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    public InMemoryWaystonesStore(List<Waystone> waystones) {
        this.waystones.addAll(waystones);
        for (final var waystone : waystones) {
            waystonesById.put(waystone.getWaystoneUid(), waystone);
            waystonesByType.put(waystone.getWaystoneKind(), waystone);
            indexPosition(waystone);
        }
    }

    @Override
    public synchronized void addWaystone(Waystone waystone) {
        waystones.add(waystone);
        waystonesById.put(waystone.getWaystoneUid(), waystone);
        waystonesByType.put(waystone.getWaystoneKind(), waystone);
        indexPosition(waystone);
    }

    @Override
    public synchronized void updateWaystone(Waystone waystone) {
        final var backingWaystone = waystonesById.get(waystone.getWaystoneUid());
        if (backingWaystone instanceof MutableWaystone mutableWaystone) {
            mutableWaystone.setName(waystone.getName());
            mutableWaystone.setVisibility(waystone.getVisibility());
            mutableWaystone.setSeen(waystone.wasSeen());
            mutableWaystone.setDimension(waystone.getDimension());
            mutableWaystone.setPos(waystone.getPos());
            indexPosition(backingWaystone);
        } else if (!(waystone instanceof WaystoneProxy) && waystone.isValid()) {
            addWaystone(waystone);
        }
    }

    @Override
    public synchronized void removeWaystone(Waystone waystone) {
        positionByWaystoneId.remove(waystone.getWaystoneUid());
        final var removedWaystone = waystonesById.remove(waystone.getWaystoneUid());
        waystones.remove(removedWaystone);
        waystonesByType.get(waystone.getWaystoneKind()).remove(removedWaystone);
    }

    @Override
    public Optional<Waystone> getWaystoneAt(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase blockEntity) {
            return Optional.of(blockEntity.getWaystone());
        }

        return Optional.empty();
    }

    @Override
    public synchronized Optional<Waystone> getWaystoneAt(GlobalPos position) {
        final var uid = positionByWaystoneId.inverse().get(position);
        return uid != null ? getWaystoneById(uid).filter(Waystone::isValid) : Optional.empty();
    }

    @Override
    public Optional<Waystone> getWaystoneById(UUID waystoneUid) {
        return Optional.ofNullable(waystonesById.get(waystoneUid));
    }

    private void indexPosition(Waystone waystone) {
        final var uid = waystone.getWaystoneUid();
        final var position = GlobalPos.of(waystone.getDimension(), waystone.getPos().immutable());
        positionByWaystoneId.forcePut(uid, position);
    }

    @Override
    public Optional<Waystone> findWaystoneByName(String name) {
        synchronized (waystones) {
            return waystones.stream().filter(it -> it.getName().getString().equals(name)).findFirst();
        }
    }

    @Override
    public List<Waystone> getWaystones() {
        synchronized (waystones) {
            return List.copyOf(waystones);
        }
    }

    @Override
    public Collection<Waystone> getWaystonesByKind(Identifier kind) {
        synchronized (waystonesByType) {
            return List.copyOf(waystonesByType.get(kind));
        }
    }

    @Override
    public List<Waystone> getGlobalWaystones() {
        synchronized (waystones) {
            return waystones.stream().filter(it -> it.getVisibility() == WaystoneVisibility.GLOBAL).toList();
        }
    }

}
