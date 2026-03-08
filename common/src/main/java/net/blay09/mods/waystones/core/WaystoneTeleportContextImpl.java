package net.blay09.mods.waystones.core;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.effect.cost.DamageItem;
import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCost;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCost;
import net.blay09.mods.shogi.common.effect.server.cooldown.AddCooldown;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownCost;
import net.blay09.mods.shogi.context.executor.EffectExecutor;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneTeleportContextImpl implements WaystoneTeleportContext {
    private final Entity entity;
    private final Waystone targetWaystone;

    private final List<Entity> additionalEntities = new ArrayList<>();
    private final List<Mob> leashedEntities = new ArrayList<>();
    private final Set<Identifier> flags = new HashSet<>();
    private EffectExecutor executor = EffectExecutor.deferred();

    private Waystone fromWaystone;

    private ItemStack warpItem = ItemStack.EMPTY;
    private InteractionHand warpHand = InteractionHand.MAIN_HAND;

    private boolean playsSound = true;
    private boolean playsEffect = true;
    private boolean appliesModifiers = true;

    private Either<List<Object>, List<Object>> requirements = Either.left(List.of());
    private boolean requirementsDirty = true;

    public WaystoneTeleportContextImpl(Entity entity, Waystone targetWaystone) {
        this.entity = entity;
        this.targetWaystone = targetWaystone;

        executor.overrideConsume(DamageItem.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().teleports.enableDurability) {
                operation.accept(value);
            }
        });

        executor.overrideConsume(ExperienceLevelCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().teleports.enableCosts) {
                operation.accept(value);
            }
        });

        executor.overrideConsume(ExperiencePointsCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().teleports.enableCosts) {
                operation.accept(value);
            }
        });

        executor.overrideConsume(CooldownCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().teleports.enableCooldowns) {
                operation.accept(value);
            }
        });
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
    public InteractionHand getWarpHand() {
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
    public boolean appliesModifiers() {
        return appliesModifiers;
    }

    @Override
    public WaystoneTeleportContext setAppliesModifiers(boolean appliesModifiers) {
        this.appliesModifiers = appliesModifiers;
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
            requirementsDirty = false;
        }
        return requirements;
    }

    @Override
    public void setRequirements(Either<List<Object>, List<Object>> warpRequirements) {
        this.requirements = warpRequirements;
        this.requirementsDirty = false;
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
    public ItemStack itemStack() {
        return warpItem;
    }

    @Override
    public Optional<Object> getVariable(String path) {
        return switch (path) {
            case "distance" ->
                    Optional.of((float) Math.sqrt(entity.distanceToSqr(targetWaystone.getPos().getCenter())));
            case "leashed" -> Optional.of((float) WaystoneTeleportManager.findLeashedAnimals(entity).size());
            case "pets" ->
                    Optional.of(entity instanceof LivingEntity livingEntity ? (float) WaystoneTeleportManager.findPets(livingEntity).size() : 0f);
            case "passengers" -> Optional.of((float) WaystoneTeleportManager.findPassengers(entity).size());
            default -> Optional.empty();
        };
    }
}
