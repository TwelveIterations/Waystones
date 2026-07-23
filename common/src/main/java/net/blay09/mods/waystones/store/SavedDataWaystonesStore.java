package net.blay09.mods.waystones.store;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.core.WaystoneIndexManager;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

import static net.blay09.mods.waystones.Waystones.id;

public class SavedDataWaystonesStore extends SavedData implements WaystonesStore {

    private static final Codec<SavedDataWaystonesStore> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WaystoneImpl.CODEC.codec().listOf().fieldOf("Waystones").forGetter(WaystonesStore::getWaystones)
    ).apply(instance, SavedDataWaystonesStore::new));

    @SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<SavedDataWaystonesStore> TYPE = new SavedDataType<>(
            id("waystones"),
            () -> new SavedDataWaystonesStore(List.of()),
            CODEC,
            null
    );

    private final WaystonesStore store;

    public SavedDataWaystonesStore(List<Waystone> waystones) {
        store = new EventfulWaystonesStore(new InMemoryWaystonesStore(waystones));
    }

    @Override
    public void addWaystone(Waystone waystone) {
        store.addWaystone(waystone);
        setDirty();
    }

    @Override
    public void updateWaystone(Waystone waystone) {
        store.updateWaystone(waystone);
        setDirty();
    }

    @Override
    public void removeWaystone(Waystone waystone) {
        WaystoneIndexManager.waystoneRemoved(waystone);
        store.removeWaystone(waystone);
        setDirty();
    }

    @Override
    public Optional<Waystone> getWaystoneAt(BlockGetter world, BlockPos pos) {
        return store.getWaystoneAt(world, pos);
    }

    @Override
    public Optional<Waystone> getWaystoneById(UUID waystoneUid) {
        return store.getWaystoneById(waystoneUid);
    }

    @Override
    public Optional<Waystone> findWaystoneByName(String name) {
        return store.findWaystoneByName(name);
    }

    @Override
    public List<Waystone> getWaystones() {
        return store.getWaystones();
    }

    @Override
    public Collection<Waystone> getWaystonesByKind(Identifier kind) {
        return store.getWaystonesByKind(kind);
    }

    @Override
    public List<Waystone> getGlobalWaystones() {
        return store.getGlobalWaystones();
    }

    public static SavedDataWaystonesStore get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }
}
