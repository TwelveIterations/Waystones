package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class WarpStoneItem extends Item implements IResetUseOnDamage {

    private final Random random = new Random();

    public WarpStoneItem(Properties properties) {
        super(properties.durability(128));
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return WaystonesConfig.getActive().general.warpStoneUseTime;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        if (WaystonesConfig.getActive().general.warpStoneUseTime <= 0 || Compat.isVivecraftInstalled) {
            return UseAnim.NONE;
        }

        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int remainingTicks) {
        if (level.isClientSide) {
            int duration = getUseDuration(itemStack, entity);
            float progress = (duration - remainingTicks) / (float) duration;
            boolean shouldMirror = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ^ entity.getMainArm() == HumanoidArm.RIGHT;
            Vec3 handOffset = new Vec3(shouldMirror ? 0.30f : -0.30f, 1f, 0.52f);
            handOffset = handOffset.yRot(-entity.getYRot() * Mth.DEG_TO_RAD);
            handOffset = handOffset.zRot(entity.getXRot() * Mth.DEG_TO_RAD);
            int maxParticles = Math.max(4, (int) (progress * 48));
            if (remainingTicks % 5 == 0) {
                for (int i = 0; i < Math.min(4, maxParticles); i++) {
                    level.addParticle(ParticleTypes.REVERSE_PORTAL,
                            entity.getX() + handOffset.x + (random.nextDouble() - 0.5) * 0.5f,
                            entity.getY() + handOffset.y + random.nextDouble(),
                            entity.getZ() + handOffset.z + (random.nextDouble() - 0.5) * 0.5f,
                            0,
                            0.05f,
                            0);
                }
                if (progress >= 0.25f) {
                    for (int i = 0; i < maxParticles; i++) {
                        level.addParticle(ParticleTypes.CRIMSON_SPORE,
                                entity.getX() + (random.nextDouble() - 0.5) * 1.5f,
                                entity.getY() + random.nextDouble(),
                                entity.getZ() + (random.nextDouble() - 0.5) * 1.5f,
                                0,
                                random.nextDouble() * 0.5f,
                                0);
                    }
                }
                if (progress >= 0.5f) {
                    for (int i = 0; i < maxParticles; i++) {
                        level.addParticle(ParticleTypes.REVERSE_PORTAL,
                                entity.getX() + (random.nextDouble() - 0.5) * 1.5f,
                                entity.getY() + random.nextDouble(),
                                entity.getZ() + (random.nextDouble() - 0.5) * 1.5f,
                                0,
                                random.nextDouble(),
                                0);
                    }
                }
                if (progress >= 0.75f) {
                    for (int i = 0; i < maxParticles / 3; i++) {
                        level.addParticle(ParticleTypes.WITCH,
                                entity.getX() + (random.nextDouble() - 0.5) * 1.5f,
                                entity.getY() + 0.5f + random.nextDouble(),
                                entity.getZ() + (random.nextDouble() - 0.5) * 1.5f,
                                0,
                                random.nextDouble(),
                                0);
                    }
                }
            }

            if (remainingTicks == 1) {
                for (int i = 0; i < maxParticles; i++) {
                    level.addParticle(ParticleTypes.REVERSE_PORTAL,
                            entity.getX() + (random.nextDouble() - 0.5) * 1.5f,
                            entity.getY() + random.nextDouble() + 1,
                            entity.getZ() + (random.nextDouble() - 0.5) * 1.5f,
                            (random.nextDouble() - 0.5) * 0,
                            random.nextDouble(),
                            (random.nextDouble() - 0.5) * 0);
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer player) {
            final var hand = player.getUsedItemHand();
            Balm.getNetworking().openGui(player, new WaystoneSelectionListBuilder(player)
                    .withTargetsForItem(itemStack)
                    .withPostTeleportHandler(context -> {
                        if (WaystonesConfig.getActive().teleports.enableDurability) {
                            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                        }
                    })
                    .buildMenuProvider(ModMenus.warpStoneSelection.get(), Component.translatable("container.waystones.waystone_selection")));
        }

        return itemStack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        final var itemStack = player.getItemInHand(hand);
        if (!player.isUsingItem() && !world.isClientSide) {
            world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
        }
        if (getUseDuration(itemStack, player) <= 0 || Compat.isVivecraftInstalled) {
            finishUsingItem(itemStack, world, player);
        } else {
            player.startUsingItem(hand);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);

    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

}
