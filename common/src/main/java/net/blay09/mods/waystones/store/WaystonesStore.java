package net.blay09.mods.waystones.store;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaystonesStore {

    void addWaystone(Waystone waystone);

    void updateWaystone(Waystone waystone);

    void removeWaystone(Waystone waystone);

    Optional<Waystone> getWaystoneAt(BlockGetter world, BlockPos pos);

    Optional<Waystone> getWaystoneById(UUID waystoneUid);

    Optional<Waystone> findWaystoneByName(String name);

    List<Waystone> getWaystones();

    Collection<Waystone> getWaystonesByKind(Identifier kind);

    List<Waystone> getGlobalWaystones();
}
