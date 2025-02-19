package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaystoneManager extends SavedData {

    private static final String DATA_NAME = Waystones.MOD_ID;
    private static final String TAG_WAYSTONES = "Waystones";
    private static final WaystoneManager clientStorageCopy = new WaystoneManager();

    private final Map<UUID, IWaystone> waystones = new HashMap<>();

    public void addWaystone(IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        setDirty();
    }

    public void updateWaystone(IWaystone waystone) {
        Waystone mutableWaystone = (Waystone) waystones.getOrDefault(waystone.getWaystoneUid(), waystone);
        mutableWaystone.setName(waystone.getName());
        mutableWaystone.setGlobal(waystone.isGlobal());
        waystones.put(waystone.getWaystoneUid(), mutableWaystone);
        setDirty();
    }

    public void removeWaystone(IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        setDirty();
    }

    public Optional<IWaystone> getWaystoneAt(BlockGetter world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase) {
            return Optional.of(((WaystoneBlockEntityBase) blockEntity).getWaystone());
        }

        return Optional.empty();
    }

    public Optional<IWaystone> getWaystoneById(UUID waystoneUid) {
        return Optional.ofNullable(waystones.get(waystoneUid));
    }

    public Optional<IWaystone> findWaystoneByName(String name) {
        return waystones.values().stream().filter(it -> it.getName().equals(name)).findFirst();
    }

    public Stream<IWaystone> getWaystonesByType(ResourceLocation type) {
        return waystones.values().stream()
                .filter(it -> it.getWaystoneType().equals(type))
                .sorted(Comparator.comparing(IWaystone::getName));
    }

    public List<IWaystone> getGlobalWaystones() {
        return waystones.values().stream().filter(IWaystone::isGlobal).collect(Collectors.toList());
    }

    public static WaystoneManager read(CompoundTag tagCompound) {
        WaystoneManager waystoneManager = new WaystoneManager();
        ListTag tagList = tagCompound.getList(TAG_WAYSTONES, Tag.TAG_COMPOUND);
        for (Tag tag : tagList) {
            CompoundTag compound = (CompoundTag) tag;
            IWaystone waystone = Waystone.read(compound);
            waystoneManager.waystones.put(waystone.getWaystoneUid(), waystone);
        }
        return waystoneManager;
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        ListTag tagList = new ListTag();
        for (IWaystone waystone : waystones.values()) {
            tagList.add(Waystone.write(waystone, new CompoundTag()));
        }
        tagCompound.put(TAG_WAYSTONES, tagList);
        return tagCompound;
    }

    public static WaystoneManager get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(WaystoneManager::read, WaystoneManager::new, DATA_NAME);
        }

        return clientStorageCopy;
    }
}
