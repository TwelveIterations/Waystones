package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.MutablePersonalizedWaystone;
import net.blay09.mods.waystones.api.PersonalizedWaystone;
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

import java.util.*;

public class PersonalizedWaystoneImpl implements MutablePersonalizedWaystone {

    public static final StreamCodec<RegistryFriendlyByteBuf, PersonalizedWaystoneImpl> STREAM_CODEC = StreamCodec.composite(
            WaystoneImpl.STREAM_CODEC,
            PersonalizedWaystoneImpl::getBackingWaystone,
            ComponentSerialization.OPTIONAL_STREAM_CODEC,
            PersonalizedWaystoneImpl::getAlias,
            ByteBufCodecs.collection(ArrayList::new, Identifier.STREAM_CODEC),
            PersonalizedWaystoneImpl::getConfiguredGroups,
            ByteBufCodecs.BOOL,
            PersonalizedWaystoneImpl::isHidden,
            PersonalizedWaystoneImpl::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MutablePersonalizedWaystone> DOWNGRADED_STREAM_CODEC = StreamCodec.of(
            (buf, waystone) -> STREAM_CODEC.encode(buf, from(waystone)),
            STREAM_CODEC::decode
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<PersonalizedWaystoneImpl>> LIST_STREAM_CODEC = ByteBufCodecs.collection(ArrayList::new, STREAM_CODEC);

    private final Waystone backingWaystone;
    private @Nullable Component alias;
    private Set<Identifier> configuredGroups;
    private boolean hidden;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private PersonalizedWaystoneImpl(Waystone backingWaystone, Optional<Component> alias, Collection<Identifier> configuredGroups, boolean hidden) {
        this(backingWaystone, alias.orElse(null), configuredGroups, hidden);
    }

    public PersonalizedWaystoneImpl(Waystone backingWaystone, @Nullable Component alias) {
        this(backingWaystone, alias, List.of());
    }

    public PersonalizedWaystoneImpl(Waystone backingWaystone, @Nullable Component alias, Collection<Identifier> configuredGroups) {
        this(backingWaystone, alias, configuredGroups, false);
    }

    public PersonalizedWaystoneImpl(Waystone backingWaystone, @Nullable Component alias, Collection<Identifier> configuredGroups, boolean hidden) {
        this.backingWaystone = backingWaystone;
        this.alias = alias;
        this.configuredGroups = Set.copyOf(configuredGroups);
        this.hidden = hidden;
    }

    public static PersonalizedWaystoneImpl from(PersonalizedWaystone waystone) {
        return waystone instanceof PersonalizedWaystoneImpl personalizedWaystone
                ? personalizedWaystone
                : new PersonalizedWaystoneImpl(waystone.getBackingWaystone(), waystone.getAlias().orElse(null), waystone.getConfiguredGroups(), waystone.isHidden());
    }

    public Waystone getBackingWaystone() {
        return backingWaystone;
    }

    public Optional<Component> getAlias() {
        return Optional.ofNullable(alias);
    }

    @Override
    public void setAlias(@Nullable Component alias) {
        this.alias = alias;
    }

    public Set<Identifier> getConfiguredGroups() {
        return configuredGroups;
    }

    @Override
    public void setConfiguredGroups(Collection<Identifier> configuredGroups) {
        this.configuredGroups = Set.copyOf(configuredGroups);
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
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
    public Optional<String> getOwnerUsername() {
        return backingWaystone.getOwnerUsername();
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

    @Override
    public Set<Identifier> getWaystoneGroups() {
        final var groups = new HashSet<>(configuredGroups);
        groups.addAll(backingWaystone.getWaystoneGroups());
        return Collections.unmodifiableSet(groups);
    }

}
