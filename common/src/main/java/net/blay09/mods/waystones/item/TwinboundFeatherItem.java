package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.TwinboundFeatherLinksComponent;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.TwinboundFeatherTargets;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TwinboundFeatherItem extends Item {

    private static final int TWINNING_DURATION = 20;
    private static final double TARGETING_RANGE = 2;
    private static final Map<UUID, TwinningProgress> twinningProgress = new ConcurrentHashMap<>();

    public TwinboundFeatherItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BRUSH;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        final var links = itemStack.get(ModComponents.twinboundFeatherLinks.get());
        return links != null && !links.links().isEmpty();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            TwinboundFeatherTargets.markLastSeen(itemStack, player);
            ensurePlayersGroup(player, itemStack);
        }
    }

    private static void ensurePlayersGroup(ServerPlayer player, ItemStack itemStack) {
        final var links = itemStack.getOrDefault(ModComponents.twinboundFeatherLinks.get(), TwinboundFeatherLinksComponent.EMPTY);
        if (links.links().isEmpty()) {
            return;
        }

        final var hasPlayersGroup = WaystoneGroups.findGroup(PlayerWaystoneManager.getWaystoneGroupRegistry(player), WaystoneGroups.PLAYERS.identifier()).isPresent();
        if (!hasPlayersGroup) {
            PlayerWaystoneManager.ensureWaystoneGroups(player, List.of(WaystoneGroups.PLAYERS));
            WaystoneSyncManager.sendWaystoneGroups(player);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final var itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTicks) {
        twinningProgress.remove(entity.getUUID());
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int remainingTicks) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof ServerPlayer player)) {
            return;
        }

        final var target = findTargetedPlayer(player);
        if (target == null || !isTouchingTips(player, target)) {
            twinningProgress.remove(player.getUUID());
            return;
        }

        final var targetItemStack = target.getUseItem();
        if (areFeathersBesties(itemStack, targetItemStack)) {
            twinningProgress.remove(player.getUUID());
            return;
        }

        spawnTwinningParticles(serverLevel, player, target);

        final var progress = twinningProgress.compute(player.getUUID(), (playerId, currentProgress) -> {
            if (currentProgress == null || !currentProgress.targetId.equals(target.getUUID())) {
                return new TwinningProgress(target.getUUID(), 1);
            }

            return new TwinningProgress(target.getUUID(), currentProgress.ticks + 1);
        });
        if (progress.ticks >= TWINNING_DURATION) {
            if (becomeBesties(itemStack, targetItemStack)) {
                serverLevel.playSound(null, player, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1f, 1.2f);
            }
            twinningProgress.remove(player.getUUID());
            twinningProgress.remove(target.getUUID());
            player.stopUsingItem();
            target.stopUsingItem();
        }
    }

    private static @Nullable ServerPlayer findTargetedPlayer(ServerPlayer player) {
        final var from = player.getEyePosition();
        final var view = player.getViewVector(1f);
        final var to = from.add(view.scale(TARGETING_RANGE));
        final var bounds = player.getBoundingBox().expandTowards(view.scale(TARGETING_RANGE)).inflate(1.0);
        final var hitResult = ProjectileUtil.getEntityHitResult(player.level(), player, from, to, bounds, TwinboundFeatherItem::canTarget, 0.3f);
        return hitResult instanceof EntityHitResult entityHitResult
                && entityHitResult.getEntity() instanceof ServerPlayer target ? target : null;
    }

    private static boolean canTarget(Entity entity) {
        return entity instanceof ServerPlayer && entity.isPickable();
    }

    private static boolean isTouchingTips(ServerPlayer player, ServerPlayer target) {
        return target.isUsingItem()
                && target.getUseItem().is(ModItems.twinboundFeather)
                && findTargetedPlayer(target) == player;
    }

    private static void spawnTwinningParticles(ServerLevel level, ServerPlayer player, ServerPlayer target) {
        if (player.tickCount % 6 != 0 || player.getUUID().compareTo(target.getUUID()) > 0) {
            return;
        }

        final var midpoint = player.position().add(target.position()).scale(0.5).add(0, 1.1, 0);
        level.sendParticles(ParticleTypes.HEART, midpoint.x, midpoint.y, midpoint.z, 1, 0.08, 0.12, 0.08, 0.01);
    }

    private static boolean areFeathersBesties(ItemStack first, ItemStack second) {
        final var firstId = first.get(ModComponents.twinboundFeather.get());
        final var secondId = second.get(ModComponents.twinboundFeather.get());
        if (firstId == null || secondId == null) {
            return false;
        }

        return first.getOrDefault(ModComponents.twinboundFeatherLinks.get(), TwinboundFeatherLinksComponent.EMPTY).links().contains(secondId)
                && second.getOrDefault(ModComponents.twinboundFeatherLinks.get(), TwinboundFeatherLinksComponent.EMPTY).links().contains(firstId);
    }

    public static boolean becomeBesties(ItemStack first, ItemStack second) {
        if (areFeathersBesties(first, second)) {
            return false;
        }

        final var firstId = getOrCreateFeatherId(first);
        final var secondId = getOrCreateFeatherId(second);
        first.set(ModComponents.twinboundFeatherLinks.get(), first.getOrDefault(ModComponents.twinboundFeatherLinks.get(), TwinboundFeatherLinksComponent.EMPTY).with(secondId));
        second.set(ModComponents.twinboundFeatherLinks.get(), second.getOrDefault(ModComponents.twinboundFeatherLinks.get(), TwinboundFeatherLinksComponent.EMPTY).with(firstId));
        return true;
    }

    private static UUID getOrCreateFeatherId(ItemStack itemStack) {
        var featherId = itemStack.get(ModComponents.twinboundFeather.get());
        if (featherId == null) {
            featherId = UUID.randomUUID();
            itemStack.set(ModComponents.twinboundFeather.get(), featherId);
        }

        return featherId;
    }

    private record TwinningProgress(UUID targetId, int ticks) {
    }
}
