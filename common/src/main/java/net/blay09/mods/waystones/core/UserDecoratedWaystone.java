package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UserDecoratedWaystone implements Waystone {

    public static final StreamCodec<RegistryFriendlyByteBuf, UserDecoratedWaystone> STREAM_CODEC = StreamCodec.composite(
            WaystoneImpl.STREAM_CODEC,
            UserDecoratedWaystone::getBackingWaystone,
            ComponentSerialization.OPTIONAL_STREAM_CODEC,
            UserDecoratedWaystone::getAlias,
            ByteBufCodecs.collection(ArrayList::new, ResourceLocation.STREAM_CODEC),
            UserDecoratedWaystone::getConfiguredGroups,
            UserDecoratedWaystone::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<UserDecoratedWaystone>> LIST_STREAM_CODEC = ByteBufCodecs.collection(ArrayList::new, STREAM_CODEC);

    private final Waystone backingWaystone;
    private final @Nullable Component alias;
    private final Set<ResourceLocation> configuredGroups;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private UserDecoratedWaystone(Waystone backingWaystone, Optional<Component> alias, Collection<ResourceLocation> configuredGroups) {
        this(backingWaystone, alias.orElse(null), configuredGroups);
    }

    public UserDecoratedWaystone(Waystone backingWaystone, @Nullable Component alias) {
        this(backingWaystone, alias, List.of());
    }

    public UserDecoratedWaystone(Waystone backingWaystone, @Nullable Component alias, Collection<ResourceLocation> configuredGroups) {
        this.backingWaystone = backingWaystone;
        this.alias = alias;
        this.configuredGroups = Set.copyOf(configuredGroups);
    }

    public Waystone getBackingWaystone() {
        return backingWaystone;
    }

    public Optional<Component> getAlias() {
        return Optional.ofNullable(alias);
    }

    public Set<ResourceLocation> getConfiguredGroups() {
        return configuredGroups;
    }

    @Override
    public UUID getWaystoneUid() {
        return backingWaystone.getWaystoneUid();
    }

    @Override
    public Component getName() {
        return backingWaystone.getName();
    }

    @Override
    public Component getEffectiveName() {
        return alias != null ? alias : getName();
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return backingWaystone.getDimension();
    }

    @Override
    public WaystoneOrigin getOrigin() {
        return backingWaystone.getOrigin();
    }

    @Override
    public boolean isOwner(Player player) {
        return backingWaystone.isOwner(player);
    }

    @Override
    public BlockPos getPos() {
        return backingWaystone.getPos();
    }

    @Override
    public boolean isValid() {
        return backingWaystone.isValid();
    }

    @Override
    public UUID getOwnerUid() {
        return backingWaystone.getOwnerUid();
    }

    @Nullable
    @Override
    public String getOwnerUsername() {
        return backingWaystone.getOwnerUsername();
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return backingWaystone.getWaystoneType();
    }

    @Override
    public boolean isValidInLevel(ServerLevel level) {
        return backingWaystone.isValidInLevel(level);
    }

    @Override
    public boolean isTransient() {
        return backingWaystone.isTransient();
    }

    @Override
    public WaystoneVisibility getVisibility() {
        return backingWaystone.getVisibility();
    }

    @Override
    public Set<ResourceLocation> getWaystoneGroups() {
        final var groups = new HashSet<>(configuredGroups);
        groups.addAll(backingWaystone.getWaystoneGroups());
        return Collections.unmodifiableSet(groups);
    }

}
