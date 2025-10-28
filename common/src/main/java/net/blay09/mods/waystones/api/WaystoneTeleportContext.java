package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WaystoneTeleportContext {
    Entity getEntity();

    Waystone getTargetWaystone();

    List<Mob> getLeashedEntities();

    List<Entity> getAdditionalEntities();

    WaystoneTeleportContext addAdditionalEntity(Entity additionalEntity);

    Optional<Waystone> getFromWaystone();

    WaystoneTeleportContext setFromWaystone(@Nullable Waystone fromWaystone);

    ItemStack getWarpItem();

    WaystoneTeleportContext setWarpItem(ItemStack warpItem);

    InteractionHand getWarpHand();

    WaystoneTeleportContext setWarpHand(InteractionHand warpHand);

    boolean isDimensionalTeleport();

    WarpRequirement getRequirements();

    WaystoneTeleportContext setRequirements(WarpRequirement warpRequirement);

    boolean playsSound();

    WaystoneTeleportContext setPlaysSound(boolean playsSound);

    boolean playsEffect();

    WaystoneTeleportContext setPlaysEffect(boolean playsEffect);

    boolean appliesModifiers();

    WaystoneTeleportContext setAppliesModifiers(boolean appliesModifiers);

    Set<ResourceLocation> getFlags();

    WaystoneTeleportContext addFlag(ResourceLocation flag);

    WaystoneTeleportContext removeFlag(ResourceLocation flag);

    default WaystoneTeleportContext addFlags(Set<ResourceLocation> flags) {
        for (ResourceLocation flag : flags) {
            addFlag(flag);
        }
        return this;
    }

    default boolean hasFlag(ResourceLocation flag) {
        return getFlags().contains(flag);
    }
}
