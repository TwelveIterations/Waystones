package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.shogi.context.ShogiContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WaystoneTeleportContext extends ShogiContext {
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

    boolean playsSound();

    WaystoneTeleportContext setPlaysSound(boolean playsSound);

    boolean playsEffect();

    WaystoneTeleportContext setPlaysEffect(boolean playsEffect);

    boolean appliesModifiers();

    WaystoneTeleportContext setAppliesModifiers(boolean appliesModifiers);

    Set<Identifier> getFlags();

    WaystoneTeleportContext addFlag(Identifier flag);

    WaystoneTeleportContext removeFlag(Identifier flag);

    Either<List<Object>, List<Object>> getRequirements();

    void setRequirements(Either<List<Object>, List<Object>> warpRequirements);

    default WaystoneTeleportContext addFlags(Set<Identifier> flags) {
        for (Identifier flag : flags) {
            addFlag(flag);
        }
        return this;
    }

    default boolean hasFlag(Identifier flag) {
        return getFlags().contains(flag);
    }
}
