package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.requirement.NoRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneTeleportContextImpl implements WaystoneTeleportContext {
    private final Entity entity;
    private final Waystone targetWaystone;

    private final List<Entity> additionalEntities = new ArrayList<>();
    private final List<Mob> leashedEntities = new ArrayList<>();
    private final Set<ResourceLocation> flags = new HashSet<>();

    private Waystone fromWaystone;

    private ItemStack warpItem = ItemStack.EMPTY;
    private InteractionHand warpHand = InteractionHand.MAIN_HAND;

    private WarpRequirement warpRequirement = NoRequirement.INSTANCE;

    private boolean playsSound = true;
    private boolean playsEffect = true;
    private boolean appliesModifiers = true;

    public WaystoneTeleportContextImpl(Entity entity, Waystone targetWaystone) {
        this.entity = entity;
        this.targetWaystone = targetWaystone;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Waystone getTargetWaystone() {
        return targetWaystone;
    }

    @Override
    public List<Mob> getLeashedEntities() {
        return leashedEntities;
    }

    @Override
    public List<Entity> getAdditionalEntities() {
        return additionalEntities;
    }

    @Override
    public WaystoneTeleportContext addAdditionalEntity(Entity additionalEntity) {
        this.additionalEntities.add(additionalEntity);
        return this;
    }

    @Override
    @Nullable
    public Optional<Waystone> getFromWaystone() {
        return Optional.ofNullable(fromWaystone);
    }

    @Override
    public WaystoneTeleportContext setFromWaystone(@Nullable Waystone fromWaystone) {
        this.fromWaystone = fromWaystone;
        return this;
    }

    @Override
    public ItemStack getWarpItem() {
        return warpItem;
    }

    @Override
    public WaystoneTeleportContext setWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    @Override
    public InteractionHand getWarpHand() {
        return warpHand;
    }

    @Override
    public WaystoneTeleportContext setWarpHand(InteractionHand warpHand) {
        this.warpHand = warpHand;
        return this;
    }

    @Override
    public boolean isDimensionalTeleport() {
        return targetWaystone.getDimension() != entity.level().dimension();
    }

    @Override
    public WarpRequirement getRequirements() {
        return warpRequirement;
    }

    @Override
    public WaystoneTeleportContext setRequirements(WarpRequirement warpRequirement) {
        this.warpRequirement = warpRequirement;
        return this;
    }

    @Override
    public boolean playsSound() {
        return playsSound;
    }

    @Override
    public WaystoneTeleportContext setPlaysSound(boolean playsSound) {
        this.playsSound = playsSound;
        return this;
    }

    @Override
    public boolean playsEffect() {
        return playsEffect;
    }

    @Override
    public WaystoneTeleportContext setPlaysEffect(boolean playsEffect) {
        this.playsEffect = playsEffect;
        return this;
    }

    @Override
    public boolean appliesModifiers() {
        return appliesModifiers;
    }

    @Override
    public WaystoneTeleportContext setAppliesModifiers(boolean appliesModifiers) {
        this.appliesModifiers = appliesModifiers;
        return this;
    }

    @Override
    public Set<ResourceLocation> getFlags() {
        return flags;
    }

    @Override
    public WaystoneTeleportContext addFlag(ResourceLocation flag) {
        flags.add(flag);
        return this;
    }

    @Override
    public WaystoneTeleportContext removeFlag(ResourceLocation flag) {
        flags.remove(flag);
        return this;
    }
}
