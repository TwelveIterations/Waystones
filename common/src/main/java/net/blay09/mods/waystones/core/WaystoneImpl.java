package net.blay09.mods.waystones.core;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneImpl implements Waystone, MutableWaystone {

    public static final StreamCodec<RegistryFriendlyByteBuf, Waystone> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            Waystone::getWaystoneType,
            UUIDUtil.STREAM_CODEC,
            Waystone::getWaystoneUid,
            ResourceKey.streamCodec(Registries.DIMENSION),
            Waystone::getDimension,
            BlockPos.STREAM_CODEC,
            Waystone::getPos,
            WaystoneOrigin.STREAM_CODEC,
            Waystone::getOrigin,
            ComponentSerialization.STREAM_CODEC,
            Waystone::getName,
            WaystoneVisibility.STREAM_CODEC,
            Waystone::getVisibility,
            WaystoneImpl::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<Waystone>> LIST_STREAM_CODEC = ByteBufCodecs.collection(ArrayList::new, STREAM_CODEC);

    public static final MapCodec<Waystone> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("Type").forGetter(Waystone::getWaystoneType),
            UUIDUtil.CODEC.fieldOf("WaystoneUid").forGetter(Waystone::getWaystoneUid),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("World").forGetter(Waystone::getDimension),
            BlockPos.CODEC.fieldOf("BlockPos").forGetter(Waystone::getPos),
            WaystoneOrigin.CODEC.fieldOf("Origin").forGetter(Waystone::getOrigin),
            UUIDUtil.CODEC.optionalFieldOf("OwnerUid").forGetter(Waystone::getOwnerUid),
            ComponentSerialization.CODEC.fieldOf("NameV2").forGetter(Waystone::getName),
            WaystoneVisibility.CODEC.fieldOf("Visibility").forGetter(Waystone::getVisibility)
    ).apply(instance, WaystoneImpl::new));

    private final ResourceLocation waystoneType;
    private final UUID waystoneUid;
    private final WaystoneOrigin origin;

    private boolean isTransient;
    private ResourceKey<Level> dimension;
    private BlockPos pos;

    private Component name = Component.empty();
    private WaystoneVisibility visibility;

    private UUID ownerUid;

    public WaystoneImpl(ResourceLocation waystoneType, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, WaystoneOrigin origin, @Nullable UUID ownerUid) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.dimension = dimension;
        this.pos = pos;
        this.origin = origin;
        this.ownerUid = ownerUid;
        this.visibility = WaystoneVisibility.fromWaystoneType(waystoneType);
    }

    public WaystoneImpl(ResourceLocation waystoneType, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, WaystoneOrigin origin, Component name, WaystoneVisibility visibility) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.dimension = dimension;
        this.pos = pos;
        this.origin = origin;
        this.name = name;
        this.visibility = visibility;
    }

    public WaystoneImpl(ResourceLocation waystoneType, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, WaystoneOrigin origin, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<UUID> ownerUid, Component name, WaystoneVisibility visibility) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.dimension = dimension;
        this.pos = pos;
        this.origin = origin;
        this.ownerUid = ownerUid.orElse(null);
        this.name = name;
        this.visibility = visibility;
    }

    @Override
    public UUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public void setName(Component name) {
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
    public WaystoneVisibility getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(WaystoneVisibility visibility) {
        this.visibility = visibility;
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
    public Optional<UUID> getOwnerUid() {
        return Optional.ofNullable(ownerUid);
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
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }
}
