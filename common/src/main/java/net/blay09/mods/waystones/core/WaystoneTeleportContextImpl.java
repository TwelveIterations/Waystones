package net.blay09.mods.waystones.core;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.effect.cost.DamageItem;
import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCost;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCost;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownCost;
import net.blay09.mods.shogi.context.executor.EffectExecutor;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.config.rules.WaystoneRuleContext;
import net.blay09.mods.waystones.config.rules.WaystonesEffectExecutors;
import net.blay09.mods.waystones.requirement.EpitaphRequirement;
import net.blay09.mods.waystones.requirement.TwinboundFeatherRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class WaystoneTeleportContextImpl implements WaystoneTeleportContext {
    private final Entity entity;
    private final Waystone targetWaystone;

    private final List<Entity> additionalEntities = new ArrayList<>();
    private final List<Mob> leashedEntities = new ArrayList<>();
    private final Set<Identifier> flags = new HashSet<>();
    private EffectExecutor executor = WaystonesEffectExecutors.deferred();

    private @Nullable Waystone fromWaystone;

    private ItemStack warpItem = ItemStack.EMPTY;
    private @Nullable InteractionHand warpHand;

    private boolean playsSound = true;
    private boolean playsEffect = true;

    private Either<List<Object>, List<Object>> requirements = Either.left(List.of());
    private boolean requirementsDirty = true;

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
    public Optional<Waystone> getFromWaystone() {
        return Optional.ofNullable(fromWaystone);
    }

    @Override
    public WaystoneTeleportContext setFromWaystone(@Nullable Waystone fromWaystone) {
        this.fromWaystone = fromWaystone;
        this.requirementsDirty = true;
        return this;
    }

    @Override
    public ItemStack getWarpItem() {
        return warpItem;
    }

    @Override
    public WaystoneTeleportContext setWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        this.requirementsDirty = true;
        return this;
    }

    @Override
    public @Nullable InteractionHand getWarpHand() {
        return warpHand;
    }

    @Override
    public WaystoneTeleportContext setWarpHand(InteractionHand warpHand) {
        this.warpHand = warpHand;
        this.requirementsDirty = true;
        return this;
    }

    @Override
    public boolean isDimensionalTeleport() {
        return targetWaystone.getDimension() != entity.level().dimension();
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
    public Set<Identifier> getFlags() {
        return flags;
    }

    @Override
    public WaystoneTeleportContext addFlag(Identifier flag) {
        if (flags.add(flag)) {
            requirementsDirty = true;
        }
        return this;
    }

    @Override
    public WaystoneTeleportContext removeFlag(Identifier flag) {
        if (flags.remove(flag)) {
            requirementsDirty = true;
        }
        return this;
    }

    @Override
    public Either<List<Object>, List<Object>> getRequirements() {
        if (requirementsDirty) {
            //noinspection unchecked
            requirements = (Either<List<Object>, List<Object>>) (Either<?, ?>) WaystonesRules.warpRequirements.get(this)
                    .mapLeft(Coercion.LIST)
                    .mapRight(Coercion.LIST);
            if (requirements.right().isPresent() && entity instanceof ServerPlayer player && player.getAbilities().instabuild) {
                requirements = requirements.swap();
            }
            if (targetWaystone.getWaystoneKind().equals(WaystoneKinds.TWINBOUND_FEATHER)) {
                requirements = requirements.map(
                        it -> Either.left(withAdditionalRequirement(it, TwinboundFeatherRequirement.INSTANCE)),
                        it -> Either.right(withAdditionalRequirement(it, TwinboundFeatherRequirement.INSTANCE))
                );
            } else if (targetWaystone.getWaystoneKind().equals(WaystoneKinds.FLEETING_MEMORIAL)) {
                requirements = requirements.map(
                        it -> Either.left(withAdditionalRequirement(it, EpitaphRequirement.INSTANCE)),
                        it -> Either.right(withAdditionalRequirement(it, EpitaphRequirement.INSTANCE))
                );
            }
            requirementsDirty = false;
        }
        return requirements;
    }

    private static List<Object> withAdditionalRequirement(List<Object> requirements, Object additionalRequirement) {
        final var result = new ArrayList<>(requirements.size() + 1);
        result.addAll(requirements);
        result.add(additionalRequirement);
        return result;
    }

    @Override
    public void setRequirements(Either<List<Object>, List<Object>> warpRequirements) {
        this.requirements = warpRequirements;
        this.requirementsDirty = false;
    }

    void invalidateRequirements() {
        this.requirementsDirty = true;
    }

    @Override
    public EffectExecutor executor() {
        return executor;
    }

    public WaystoneTeleportContextImpl setExecutor(EffectExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public Level level() {
        return entity.level();
    }

    @Override
    public Entity entity() {
        return entity;
    }

    @Override
    public BlockPos blockPos() {
        return entity.blockPosition();
    }

    @Override
    public BlockState blockState() {
        return level().getBlockState(blockPos());
    }

    @Override
    public @Nullable BlockEntity blockEntity() {
        return level().getBlockEntity(blockPos());
    }

    @Override
    public ItemStack itemStack() {
        return warpItem;
    }

    @Override
    public Optional<Object> getVariable(String path) {
        return switch (path) {
            case WaystoneRuleContext.SOURCE_WAYSTONE_VARIABLE -> Optional.ofNullable(getFromWaystone());
            case WaystoneRuleContext.TARGET_WAYSTONE_VARIABLE -> Optional.of(targetWaystone);
            case WaystoneRuleContext.FLAGS_VARIABLE -> Optional.of(flags);
            case "distance" ->
                    Optional.of((float) Math.sqrt(entity.distanceToSqr(Vec3.atCenterOf(targetWaystone.getPos()))));
            case "leashed" -> Optional.of((float) WaystoneTeleportManager.findLeashedAnimals(entity).size());
            case "pets" ->
                    Optional.of(entity instanceof LivingEntity livingEntity ? (float) WaystoneTeleportManager.findPets(livingEntity).size() : 0f);
            case "passengers" -> Optional.of((float) WaystoneTeleportManager.findPassengers(entity).size());
            default -> Optional.empty();
        };
    }
}
