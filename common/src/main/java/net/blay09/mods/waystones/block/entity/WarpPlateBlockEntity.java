package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.WaystoneReferenceComponent;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGeneratorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;


public class WarpPlateBlockEntity extends WaystoneBlockEntityBase {

    private final WeakHashMap<Entity, Integer> ticksPassedPerEntity = new WeakHashMap<>();

    private final Random random = new Random();

    private int nextRoundRobinAttunementSlot;

    protected int attunementTicks;

    public WarpPlateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.warpPlate.value(), blockPos, blockState);
    }

    @Override
    protected void onInventoryChanged() {
        if (level != null) {
            level.setBlock(worldPosition, getIdleState(), 3);
        }
    }

    @Override
    public void initializeWaystone(ServerLevelAccessor level, @Nullable LivingEntity player, WaystoneOrigin origin) {
        super.initializeWaystone(level, player, origin);

        // Warp Plates generate a name on placement always
        final var waystone = getWaystone();
        if (waystone instanceof MutableWaystone) {
            final var name = NameGeneratorManager.get(level.getLevel().getServer()).getName(level, waystone, level.getRandom(), NameGenerationMode.RANDOM_ONLY);
            ((MutableWaystone) waystone).setName(name);
        }

        WaystoneSyncManager.sendWaystoneUpdateToAll(level.getServer(), waystone);

        initializeInventory();
    }

    private void initializeInventory() {
        setShardItem(ModItems.dormantShard.createStack());
    }

    @Override
    protected Identifier getWaystoneKind() {
        return WaystoneKinds.WARP_PLATE;
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("LastAttunementSlot", nextRoundRobinAttunementSlot);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        nextRoundRobinAttunementSlot = input.getIntOr("LastAttunementSlot", 0);
    }

    public boolean hasPotentialWarpTarget() {
        final var shardItem = getShardItem();
        return !shardItem.isEmpty();
    }

    @Override
    public Component getName() {
        return Component.translatable("container.waystones.warp_plate");
    }

    public void onEntityCollision(Entity entity) {
        if (WaystonePermissionManager.isEntityDeniedTeleports(entity)) {
            return;
        }

        final var blockState = getBlockState();
        final var previousStatus = blockState.getValue(WarpPlateBlock.STATUS);
        if (previousStatus == WarpPlateBlock.WarpPlateStatus.IDLE) {
            final var ticksPassed = ticksPassedPerEntity.putIfAbsent(entity, 0);
            if ((ticksPassed == null || ticksPassed != -1) && hasPotentialWarpTarget()) {
                final var targetWaystone = getTargetWaystone().orElse(InvalidWaystone.INSTANCE);
                final var canAfford = WaystonesAPI.createUncheckedDefaultTeleportContext(entity, targetWaystone, it -> it.setFromWaystone(getWaystone()))
                        .mapLeft(WaystoneTeleportContext::getRequirements)
                        .mapLeft(it -> it.left().isPresent())
                        .left().orElse(true);
                final var status = targetWaystone.isValid() && canAfford ? WarpPlateBlock.WarpPlateStatus.WARPING : WarpPlateBlock.WarpPlateStatus.WARPING_INVALID;
                level.setBlock(worldPosition, blockState.setValue(WarpPlateBlock.STATUS, status), Block.UPDATE_ALL);
            }
        }
    }

    public BlockState getIdleState() {
        final var shardItem = getShardItem();
        if (shardItem.isEmpty()) {
            return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.EMPTY);
        } else if (shardItem.is(ModItems.dormantShard.asItem())) {
            return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.ATTUNING);
        }
        return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.IDLE);
    }

    public List<Entity> getEntitiesOnTop() {
        final var boundsAbove = new AABB(worldPosition.getX(),
                worldPosition.getY(),
                worldPosition.getZ(),
                worldPosition.getX() + 1,
                worldPosition.getY() + 1,
                worldPosition.getZ() + 1);
        return level.getEntities((Entity) null, boundsAbove, EntitySelector.ENTITY_STILL_ALIVE);
    }

    public void serverTick() {
        attuneShard();

        List<Entity> entitiesOnTop = null;
        final var status = getBlockState().getValue(WarpPlateBlock.STATUS);
        if (status == WarpPlateBlock.WarpPlateStatus.WARPING
                || status == WarpPlateBlock.WarpPlateStatus.WARPING_INVALID) {
            entitiesOnTop = getEntitiesOnTop();
            if (entitiesOnTop.isEmpty()) {
                level.setBlock(worldPosition, getIdleState(), Block.UPDATE_ALL);
                ticksPassedPerEntity.clear();
            }
        }

        if (hasPotentialWarpTarget()) {
            final var useTime = getWarpPlateUseTime();
            Iterator<Map.Entry<Entity, Integer>> iterator = ticksPassedPerEntity.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Entity, Integer> entry = iterator.next();
                Entity entity = entry.getKey();
                Integer ticksPassed = entry.getValue();
                if (entitiesOnTop == null) {
                    entitiesOnTop = getEntitiesOnTop();
                }
                if (!entity.isAlive() || !entitiesOnTop.contains(entity)) {
                    iterator.remove();
                } else if (ticksPassed > useTime) {
                    ItemStack targetAttunementStack = getTargetAttunementStack();
                    nextRoundRobinAttunementSlot++;
                    Waystone targetWaystone = WaystonesAPI.getBoundWaystone(null, targetAttunementStack).orElse(null);
                    if (targetWaystone != null && targetWaystone.isValid()) {
                        teleportToTarget(entity, targetWaystone, targetAttunementStack);
                    }

                    if (entity instanceof Player) {
                        if (targetWaystone == null) {
                            var chatComponent = Component.translatable("chat.waystones.warp_plate_has_no_target");
                            chatComponent.withStyle(ChatFormatting.DARK_RED);
                            ((Player) entity).sendOverlayMessage(chatComponent);
                        } else if (!targetWaystone.isValid()) {
                            var chatComponent = Component.translatable("chat.waystones.warp_plate_has_invalid_target");
                            chatComponent.withStyle(ChatFormatting.DARK_RED);
                            ((Player) entity).sendOverlayMessage(chatComponent);
                        }
                    }

                    iterator.remove();
                } else if (ticksPassed != -1) {
                    entry.setValue(ticksPassed + 1);
                }
            }
        }
    }

    private int getWarpPlateUseTime() {
        return WaystonesRules.warpPlateUseTime.getOrDefault(this);
    }

    private void teleportToTarget(Entity entity, Waystone targetWaystone, ItemStack targetAttunementStack) {
        WaystonesAPI.createUncheckedDefaultTeleportContext(entity, targetWaystone, it -> {
                    it.setFromWaystone(getWaystone());
                    it.setWarpItem(targetAttunementStack);
                })
                .ifLeft(context -> WaystonesAPI.tryTeleportAsync(context)
                        .thenAccept(result -> result
                                .ifRight(informRejectedTeleport(entity))
                                .ifLeft(_ -> {
                                    if (targetAttunementStack.is(ModItemTags.SINGLE_USE_WARP_SHARDS)) {
                                        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                                            targetAttunementStack.shrink(1);
                                        }
                                    }
                                })))
                .ifRight(informRejectedTeleport(entity))
                .left();
    }

    private Consumer<WaystoneTeleportError> informRejectedTeleport(final Entity entityToInform) {
        return error -> {
            if (entityToInform instanceof Player player) {
                var chatComponent = error.getComponent().copy().withStyle(ChatFormatting.DARK_RED);
                player.sendOverlayMessage(chatComponent);
            }
        };
    }

    public ItemStack getTargetAttunementStack() {
        boolean shouldRoundRobin = false;
        boolean shouldPrioritizeSingleUseShards = false;
        List<ItemStack> attunedShards = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.is(ModItemTags.WARP_SHARDS)) {
                Waystone waystoneAttunedTo = WaystonesAPI.getBoundWaystone(null, itemStack).orElse(null);
                if (waystoneAttunedTo != null && !waystoneAttunedTo.getWaystoneUid().equals(getWaystone().getWaystoneUid())) {
                    attunedShards.add(itemStack);
                }
            } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_PREFERS_ROUND_ROBIN)) {
                shouldRoundRobin = true;
            } else if (itemStack.is(ModItemTags.WARP_MODIFIERS_PREFERS_SINGLE_USE)) {
                shouldPrioritizeSingleUseShards = true;
            }
        }
        if (shouldPrioritizeSingleUseShards && attunedShards.stream().anyMatch(stack -> stack.is(ModItemTags.SINGLE_USE_WARP_SHARDS))) {
            attunedShards.removeIf(stack -> !stack.is(ModItemTags.SINGLE_USE_WARP_SHARDS));
        }

        if (!attunedShards.isEmpty()) {
            nextRoundRobinAttunementSlot = nextRoundRobinAttunementSlot % attunedShards.size();
            return shouldRoundRobin ? attunedShards.get(nextRoundRobinAttunementSlot) : attunedShards.get(random.nextInt(attunedShards.size()));
        }

        return ItemStack.EMPTY;
    }

    public Optional<Waystone> getTargetWaystone() {
        return WaystonesAPI.getBoundWaystone(null, getTargetAttunementStack());
    }

    public void markEntityForCooldown(Entity entity) {
        ticksPassedPerEntity.put(entity, -1);
    }

    public void setShardItem(ItemStack itemStack) {
        container.setItem(0, itemStack);
        if (level != null) {
            level.setBlock(worldPosition, getIdleState(), 3);
        }
        setChanged();
    }

    public ItemStack getShardItem() {
        return container.getItem(0);
    }

    public void attuneShard() {
        final var shardItem = getShardItem();
        if (shardItem.is(ModItems.dormantShard.asItem())) {
            attunementTicks++;

            if (attunementTicks >= getMaxAttunementTicks()) {
                attunementTicks = 0;
                final var attunedShard = ModItems.attunedShard.createStack();
                WaystonesAPI.setBoundWaystone(attunedShard, getWaystone());
                setShardItem(attunedShard);
            }
        } else if (level != null && shardItem.is(ModItems.attunedShard.asItem())) {
            WaystonesAPI.getBoundWaystone(null, shardItem).ifPresent(it -> {
                if (it.getWaystoneUid().equals(getWaystone().getWaystoneUid())) {
                    final var shardEntity = new ItemEntity(level,
                            worldPosition.getX() + 0.5,
                            worldPosition.getY() + 0.5,
                            worldPosition.getZ() + 0.5,
                            shardItem);
                    level.addFreshEntity(shardEntity);
                    setShardItem(ItemStack.EMPTY);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMALL_GUST, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.5);
                        level.playSound(null, worldPosition, SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1f, 1f);
                    }
                }
            });
        } else {
            attunementTicks = 0;
        }
    }

    public int getMaxAttunementTicks() {
        return 30;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(ModComponents.waystoneIdentity.value(), new WaystoneReferenceComponent(getEffectiveWaystoneUid(), WarpPlateBlock.getGalacticName(getWaystone().getWaystoneUid())));
    }
}
