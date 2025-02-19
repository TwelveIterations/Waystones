package net.blay09.mods.waystones.core;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Waystone implements IWaystone, IMutableWaystone {

    private final ResourceLocation waystoneType;
    private final UUID waystoneUid;
    private final WaystoneOrigin origin;

    private ResourceKey<Level> dimension;
    private BlockPos pos;

    private String name = "";
    private boolean isGlobal;

    private UUID ownerUid;

    public Waystone(ResourceLocation waystoneType, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, WaystoneOrigin origin, @Nullable UUID ownerUid) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.dimension = dimension;
        this.pos = pos;
        this.origin = origin;
        this.ownerUid = ownerUid;
    }

    @Override
    public UUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public WaystoneOrigin getOrigin() {
        return origin;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    @Override
    public boolean isOwner(Player player) {
        return ownerUid == null || player.getGameProfile().getId().equals(ownerUid) || player.getAbilities().instabuild;
    }

    @Override
    public void setOwnerUid(@Nullable UUID ownerUid) {
        this.ownerUid = ownerUid;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public UUID getOwnerUid() {
        return ownerUid;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    @Override
    public boolean isValidInLevel(ServerLevel level) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModBlockTags.IS_TELEPORT_TARGET);
    }

    @Override
    public TeleportDestination resolveDestination(ServerLevel level) {
        BlockState state = level.getBlockState(pos);
        Direction direction = state.getValue(WaystoneBlock.FACING);
        // Use a list to keep order intact - it might check one direction twice, but no one cares
        List<Direction> directionCandidates = Lists.newArrayList(direction, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
        for (Direction candidate : directionCandidates) {
            BlockPos offsetPos = pos.relative(candidate);
            BlockPos offsetPosUp = offsetPos.above();
            if (level.getBlockState(offsetPos).isSuffocating(level, offsetPos) || level.getBlockState(offsetPosUp).isSuffocating(level, offsetPosUp)) {
                continue;
            }

            direction = candidate;
            break;
        }

        BlockPos targetPos = getWaystoneType().equals(WaystoneTypes.WARP_PLATE) ? getPos() : getPos().relative(direction);
        Vec3 location = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        return new TeleportDestination(level, location, direction);
    }

    public static IWaystone read(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        ResourceLocation waystoneType = buf.readResourceLocation();
        String name = buf.readUtf();
        boolean isGlobal = buf.readBoolean();
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(250)));
        BlockPos pos = buf.readBlockPos();
        WaystoneOrigin origin = buf.readEnum(WaystoneOrigin.class);

        Waystone waystone = new Waystone(waystoneType, waystoneUid, dimension, pos, origin, null);
        waystone.setName(name);
        waystone.setGlobal(isGlobal);
        return waystone;
    }

    public static IWaystone read(CompoundTag compound) {
        UUID waystoneUid = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("WaystoneUid")));
        String name = compound.getString("Name");
        ResourceKey<Level> dimensionType = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("World")));
        BlockPos pos = NbtUtils.readBlockPos(compound.getCompound("BlockPos"));
        boolean wasGenerated = compound.getBoolean("WasGenerated"); // legacy
        WaystoneOrigin origin = wasGenerated ? WaystoneOrigin.WILDERNESS : WaystoneOrigin.UNKNOWN;
        if (compound.contains("Origin")) {
            try {
                origin = WaystoneOrigin.valueOf(compound.getString("Origin"));
            } catch (IllegalArgumentException ignored) {
            }
        }
        UUID ownerUid = compound.contains("OwnerUid") ? NbtUtils.loadUUID(Objects.requireNonNull(compound.get("OwnerUid"))) : null;
        ResourceLocation waystoneType = compound.contains("Type") ? new ResourceLocation(compound.getString("Type")) : WaystoneTypes.WAYSTONE;
        Waystone waystone = new Waystone(waystoneType, waystoneUid, dimensionType, pos, origin, ownerUid);
        waystone.setName(name);
        waystone.setGlobal(compound.getBoolean("IsGlobal"));
        return waystone;
    }

    public static void write(FriendlyByteBuf buf, IWaystone waystone) {
        buf.writeUUID(waystone.getWaystoneUid());
        buf.writeResourceLocation(waystone.getWaystoneType());
        buf.writeUtf(waystone.getName());
        buf.writeBoolean(waystone.isGlobal());
        buf.writeResourceLocation(waystone.getDimension().location());
        buf.writeBlockPos(waystone.getPos());
        buf.writeEnum(waystone.getOrigin());
    }

    public static CompoundTag write(IWaystone waystone, CompoundTag compound) {
        compound.put("WaystoneUid", NbtUtils.createUUID(waystone.getWaystoneUid()));
        compound.putString("Type", waystone.getWaystoneType().toString());
        compound.putString("Name", waystone.getName());
        compound.putString("World", waystone.getDimension().location().toString());
        compound.put("BlockPos", NbtUtils.writeBlockPos(waystone.getPos()));
        compound.putString("Origin", waystone.getOrigin().name());
        if (waystone.getOwnerUid() != null) {
            compound.put("OwnerUid", NbtUtils.createUUID(waystone.getOwnerUid()));
        }
        compound.putBoolean("IsGlobal", waystone.isGlobal());
        return compound;
    }
}
