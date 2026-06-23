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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDecoratedWaystone implements Waystone {

    public static final StreamCodec<RegistryFriendlyByteBuf, UserDecoratedWaystone> STREAM_CODEC = StreamCodec.composite(
            WaystoneImpl.STREAM_CODEC,
            UserDecoratedWaystone::getBackingWaystone,
            ComponentSerialization.OPTIONAL_STREAM_CODEC,
            UserDecoratedWaystone::getAlias,
            UserDecoratedWaystone::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<UserDecoratedWaystone>> LIST_STREAM_CODEC = ByteBufCodecs.collection(ArrayList::new, STREAM_CODEC);

    private final Waystone backingWaystone;
    private final @Nullable Component alias;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private UserDecoratedWaystone(Waystone backingWaystone, Optional<Component> alias) {
        this(backingWaystone, alias.orElse(null));
    }

    public UserDecoratedWaystone(Waystone backingWaystone, @Nullable Component alias) {
        this.backingWaystone = backingWaystone;
        this.alias = alias;
    }

    public Waystone getBackingWaystone() {
        return backingWaystone;
    }

    public Optional<Component> getAlias() {
        return Optional.ofNullable(alias);
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
    public Optional<UUID> getOwnerUid() {
        return backingWaystone.getOwnerUid();
    }

    @Override
    public Identifier getWaystoneKind() {
        return backingWaystone.getWaystoneKind();
    }

    @Override
    public boolean wasSeen() {
        return backingWaystone.wasSeen();
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
}
