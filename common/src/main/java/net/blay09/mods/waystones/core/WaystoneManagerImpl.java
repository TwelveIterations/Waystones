package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneManager;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.api.event.WaystonesLoadedEvent;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaystoneManagerImpl extends SavedData implements WaystoneManager {

    private static final Logger logger = LoggerFactory.getLogger(WaystoneManagerImpl.class);

    private static final String DATA_NAME = Waystones.MOD_ID;
    private static final String TAG_WAYSTONES = "Waystones";
    private static final WaystoneManagerImpl clientStorageCopy = new WaystoneManagerImpl();

    private final Map<UUID, Waystone> waystones = new HashMap<>();

    public void addWaystone(Waystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneInitializedEvent(waystone));
    }

    public void updateWaystone(Waystone waystone) {
        final var backingWaystone = waystone instanceof PersonalizedWaystoneImpl personalizedWaystone ? personalizedWaystone.getBackingWaystone() : waystone;
        final var existingWaystone = waystones.getOrDefault(backingWaystone.getWaystoneUid(), backingWaystone);
        final var existingBackingWaystone = existingWaystone instanceof PersonalizedWaystoneImpl personalizedWaystone ? personalizedWaystone.getBackingWaystone() : existingWaystone;
        WaystoneImpl mutableWaystone = (WaystoneImpl) existingBackingWaystone;
        mutableWaystone.setName(waystone.getName());
        mutableWaystone.setVisibility(waystone.getVisibility());
        waystones.put(backingWaystone.getWaystoneUid(), mutableWaystone);
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneUpdatedEvent(waystone));
    }

    public void removeWaystone(Waystone waystone) {
        WaystoneIndexManager.waystoneRemoved(waystone);
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
        return waystones.values().stream().filter(it -> it.getName().getString().equals(name)).findFirst();
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

    public static WaystoneManagerImpl read(CompoundTag tagCompound, HolderLookup.Provider provider) {
        WaystoneManagerImpl waystoneManager = new WaystoneManagerImpl();
        ListTag tagList = tagCompound.getList(TAG_WAYSTONES, Tag.TAG_COMPOUND);
        for (Tag tag : tagList) {
            CompoundTag compound = (CompoundTag) tag;
            Waystone waystone = WaystoneImpl.read(compound, provider);
            // Remove unnamed sharestones as a cleanup workaround for #1133.
            if (!waystone.hasName() && WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                continue;
            }
            waystoneManager.waystones.put(waystone.getWaystoneUid(), waystone);
        }
        try {
            Balm.getEvents().fireEvent(new WaystonesLoadedEvent(waystoneManager));
        } catch (Exception e) {
            logger.error("Error while firing WaystonesLoadedEvent", e);
        }
        return waystoneManager;
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound, HolderLookup.Provider provider) {
        ListTag tagList = new ListTag();
        for (Waystone waystone : waystones.values()) {
            tagList.add(WaystoneImpl.write(waystone, new CompoundTag(), provider));
        }
        tagCompound.put(TAG_WAYSTONES, tagList);
        return tagCompound;
    }

    public static WaystoneManagerImpl get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(new Factory<>(WaystoneManagerImpl::new, WaystoneManagerImpl::read, null), DATA_NAME);
        }

        return clientStorageCopy;
    }
}
