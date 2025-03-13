package net.blay09.mods.waystones.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaystoneManagerImpl extends SavedData implements WaystoneManager {

    private static final String DATA_NAME = Waystones.MOD_ID + "_waystones";
    private static final Codec<WaystoneManagerImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WaystoneImpl.CODEC.codec().listOf().fieldOf("waystones").forGetter(it -> it.getWaystones().toList())
    ).apply(instance, WaystoneManagerImpl::new));

    public static final SavedDataType<WaystoneManagerImpl> TYPE = new SavedDataType<>(
            DATA_NAME,
            (context) -> new WaystoneManagerImpl(List.of()),
            ctx -> CODEC,
            null // TODO this can't be null but mod loaders will save us soon I'm sure
    );

    private final Map<UUID, Waystone> waystones = new HashMap<>();

    public WaystoneManagerImpl(List<Waystone> waystones) {
        for (final var waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
        }
    }

    public void addWaystone(Waystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneInitializedEvent(waystone));
    }

    public void updateWaystone(Waystone waystone) {
        WaystoneImpl mutableWaystone = (WaystoneImpl) waystones.getOrDefault(waystone.getWaystoneUid(), waystone);
        mutableWaystone.setName(waystone.getName());
        mutableWaystone.setVisibility(waystone.getVisibility());
        waystones.put(waystone.getWaystoneUid(), mutableWaystone);
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneUpdatedEvent(waystone));
    }

    public void removeWaystone(Waystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneRemovedEvent(waystone));
    }

    @Override
    public Optional<Waystone> getWaystoneAt(BlockGetter world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase) {
            return Optional.of(((WaystoneBlockEntityBase) blockEntity).getWaystone());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Waystone> getWaystoneById(UUID waystoneUid) {
        return Optional.ofNullable(waystones.get(waystoneUid));
    }

    @Override
    public Optional<Waystone> findWaystoneByName(String name) {
        return waystones.values().stream().filter(it -> it.getName().equals(name)).findFirst();
    }

    @Override
    public Stream<Waystone> getWaystones() {
        return waystones.values().stream();
    }

    @Override
    public Stream<Waystone> getWaystonesByType(ResourceLocation type) {
        return waystones.values().stream()
                .filter(it -> it.getWaystoneType().equals(type));
    }

    @Override
    public List<Waystone> getGlobalWaystones() {
        return waystones.values().stream().filter(it -> it.getVisibility() == WaystoneVisibility.GLOBAL).collect(Collectors.toList());
    }

    public static WaystoneManagerImpl get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(TYPE);
        }

        throw new IllegalStateException("Cannot get WaystoneManager from client");
    }
}
