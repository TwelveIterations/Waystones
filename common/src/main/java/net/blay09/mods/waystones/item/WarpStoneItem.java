package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class WarpStoneItem extends Item implements IResetUseOnDamage {

    private final Random random = new Random();

    private static final BalmMenuProvider containerProvider = new BalmMenuProvider() {
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.waystones.waystone_selection");
        }

        @Override
        public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return WaystoneSelectionMenu.createWaystoneSelection(i, playerEntity, WarpMode.WARP_STONE, null);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            buf.writeByte(WarpMode.WARP_STONE.ordinal());
        }
    };

    public WarpStoneItem(Properties properties) {
        super(properties.durability(100));
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().cooldowns.warpStoneUseTime;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        if (getUseDuration(itemStack) <= 0 || Compat.isVivecraftInstalled) {
            return UseAnim.NONE;
        }

        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int remainingTicks) {
        if (level.isClientSide) {
            int duration = getUseDuration(itemStack);
            float progress = (duration - remainingTicks) / (float) duration;
            boolean shouldMirror = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ^ entity.getMainArm() == HumanoidArm.RIGHT;
            Vec3 handOffset = new Vec3(shouldMirror ? 0.30f : -0.30f, 1f, 0.52f);
            handOffset = handOffset.yRot( -entity.getYRot() * Mth.DEG_TO_RAD);
            handOffset = handOffset.zRot( entity.getXRot() * Mth.DEG_TO_RAD);
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
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Balm.getNetworking().openGui(((ServerPlayer) entity), containerProvider);
        }

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Reset cooldown when using in creative mode
        if (player.getAbilities().instabuild) {
            PlayerWaystoneManager.setWarpStoneCooldownUntil(player, 0);
        }

        if (PlayerWaystoneManager.canUseWarpStone(player, itemStack)) {
            if (!player.isUsingItem() && !world.isClientSide) {
                world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
            }
            if (getUseDuration(itemStack) <= 0 || Compat.isVivecraftInstalled) {
                finishUsingItem(itemStack, world, player);
            } else {
                player.startUsingItem(hand);
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        } else {
            var chatComponent = Component.translatable("chat.waystones.warpstone_not_charged");
            chatComponent.withStyle(ChatFormatting.RED);
            player.displayClientMessage(chatComponent, true);
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return getBarWidth(itemStack) < MAX_BAR_WIDTH;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        Player player = Balm.getProxy().getClientPlayer();
        if (player == null) {
            return MAX_BAR_WIDTH;
        }

        long timeLeft = PlayerWaystoneManager.getWarpStoneCooldownLeft(player);
        int maxCooldown = WaystonesConfig.getActive().cooldowns.warpStoneCooldown * 1000;
        if (maxCooldown == 0) {
            return MAX_BAR_WIDTH;
        }

        return Math.round(MAX_BAR_WIDTH - (float) timeLeft * MAX_BAR_WIDTH / (float) maxCooldown);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        Player player = Balm.getProxy().getClientPlayer();
        return player != null ? PlayerWaystoneManager.canUseWarpStone(player, itemStack) || super.isFoil(itemStack) : super.isFoil(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        Player player = Balm.getProxy().getClientPlayer();
        if (player == null) {
            return;
        }

        long timeLeft = PlayerWaystoneManager.getWarpStoneCooldownLeft(player);
        int secondsLeft = (int) (timeLeft / 1000);
        if (secondsLeft > 0) {
            var secondsLeftText = Component.translatable("tooltip.waystones.cooldown_left", secondsLeft);
            secondsLeftText.withStyle(ChatFormatting.GOLD);
            list.add(secondsLeftText);
        }
    }

}
