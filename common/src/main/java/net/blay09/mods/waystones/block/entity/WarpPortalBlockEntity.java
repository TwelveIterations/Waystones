package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.WarpPortalBlock;
import net.blay09.mods.waystones.core.InvalidWaystone;
import net.blay09.mods.waystones.core.WarpPortalManager;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class WarpPortalBlockEntity extends BlockEntity {

    public static final int LIFETIME_TICKS = 20 * 60 * 5;

    private Waystone waystone = InvalidWaystone.INSTANCE;
    private Waystone targetWaystone = InvalidWaystone.INSTANCE;
    private @Nullable UUID waystoneUid;
    private @Nullable UUID targetWaystoneUid;
    private int age;

    public WarpPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.warpPortal.value(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.storeNullable("UUID", UUIDUtil.CODEC, waystoneUid);
        output.storeNullable("Target", UUIDUtil.CODEC, targetWaystoneUid);
        output.putInt("Age", age);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        input.read("UUID", UUIDUtil.CODEC).ifPresent(uuid -> waystoneUid = uuid);
        input.read("Target", UUIDUtil.CODEC).ifPresent(uuid -> targetWaystoneUid = uuid);
        age = input.getIntOr("Age", 0);
    }

    public void initialize(ServerLevel level, Waystone targetWaystone) {
        this.targetWaystone = targetWaystone;
        targetWaystoneUid = targetWaystone.getWaystoneUid();
        waystoneUid = UUID.randomUUID();

        final var portalWaystone = new WaystoneImpl(WaystoneKinds.WARP_PORTAL,
                waystoneUid,
                level.dimension(),
                worldPosition,
                WaystoneOrigin.PLAYER,
                null);
        portalWaystone.setName(Component.translatable("gui.waystones.waystone_selection.return_to_portal"));
        portalWaystone.setTransient(true);
        portalWaystone.setSeen(true);
        waystone = portalWaystone;
        SavedDataWaystonesStore.get(level.getServer()).addWaystone(portalWaystone);
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    public Waystone getWaystone() {
        if (!waystone.isValid() && waystoneUid != null && level instanceof ServerLevel serverLevel) {
            waystone = new WaystoneProxy(serverLevel.getServer(), waystoneUid);
        }

        return waystone;
    }

    private Waystone getTargetWaystone() {
        if (!targetWaystone.isValid() && targetWaystoneUid != null && level instanceof ServerLevel serverLevel) {
            targetWaystone = new WaystoneProxy(serverLevel.getServer(), targetWaystoneUid);
        }

        return targetWaystone;
    }

    public void onEntityInside(Entity entity) {
        if (level instanceof ServerLevel serverLevel && WarpPortalManager.canUsePortal(entity, this)) {
            final var target = getTargetWaystone();
            if (target.isValid()) {
                WarpPortalManager.teleportFromPortal(entity, this, target);
            } else {
                if (entity instanceof Player player) {
                    player.sendSystemMessage(Component.translatable("chat.waystones.warp_portal_attunement_lost"));
                }
                removePortal(serverLevel);
            }
        }
    }

    public void serverTick() {
        if (level instanceof ServerLevel serverLevel && ++age >= LIFETIME_TICKS) {
            removePortal(serverLevel);
        }
    }

    public void removePortal(ServerLevel level) {
        final var portalWaystone = getWaystone();
        if (portalWaystone.isValid()) {
            SavedDataWaystonesStore.get(level.getServer()).removeWaystone(portalWaystone);
        }

        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.2f);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, 32, 0.5, 0.75, 0.5, 0.05);
        level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);
        final var above = worldPosition.above();
        if (level.getBlockState(above).is(getBlockState().getBlock())) {
            level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        final var portalWaystone = getWaystone();
        if (level instanceof ServerLevel serverLevel && state.getValue(WarpPortalBlock.HALF) == DoubleBlockHalf.LOWER && portalWaystone.isValid()) {
            SavedDataWaystonesStore.get(serverLevel.getServer()).removeWaystone(portalWaystone);
        }
    }

    public static void spawnIdleParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.PORTAL,
                    pos.getX() + random.nextDouble(),
                    pos.getY(),
                    pos.getZ() + random.nextDouble(),
                    (random.nextDouble() - 0.5) * 0.2,
                    0,
                    (random.nextDouble() - 0.5) * 0.2);
        }
    }
}
