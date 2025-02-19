package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BoundScrollItem extends ScrollItemBase implements IResetUseOnDamage, IFOVOnUse, IAttunementItem {

    public BoundScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().cooldowns.scrollUseTime;
    }

    /**
     * @deprecated Use {@link #setWaystoneAttunedTo(ItemStack, IWaystone)} instead.
     */
    @Deprecated
    public static void setBoundTo(ItemStack itemStack, @Nullable IWaystone entry) {
        WaystonesAPI.setBoundWaystone(itemStack, entry);
    }

    /**
     * @deprecated Use {@link #getWaystoneAttunedTo(MinecraftServer, ItemStack)} instead.
     */
    @Nullable
    @Deprecated
    protected IWaystone getBoundTo(Player player, ItemStack itemStack) {
        return getWaystoneAttunedTo(player.getServer(), itemStack);
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable IWaystone waystone) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
            itemStack.setTag(tagCompound);
        }

        if (waystone != null) {
            tagCompound.put("WaystonesBoundTo", NbtUtils.createUUID(waystone.getWaystoneUid()));
        } else {
            tagCompound.remove("WaystonesBoundTo");
        }
    }

    @Override
    public @Nullable IWaystone getWaystoneAttunedTo(@Nullable MinecraftServer server, ItemStack itemStack) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound != null && tagCompound.contains("WaystonesBoundTo", Tag.TAG_INT_ARRAY)) {
            return new WaystoneProxy(server, NbtUtils.loadUUID(Objects.requireNonNull(tagCompound.get("WaystonesBoundTo"))));
        }

        return null;
    }

    protected WarpMode getWarpMode() {
        return WarpMode.BOUND_SCROLL;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(context.getHand());
        Level world = context.getLevel();
        BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if (tileEntity instanceof WaystoneBlockEntity) {
            IWaystone waystone = ((WaystoneBlockEntity) tileEntity).getWaystone();
            if (!PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
                PlayerWaystoneManager.activateWaystone(player, waystone);
            }

            if (!world.isClientSide) {
                ItemStack boundItem = heldItem.getCount() == 1 ? heldItem : heldItem.split(1);
                WaystonesAPI.setBoundWaystone(boundItem, waystone);
                if (boundItem != heldItem) {
                    if (!player.addItem(boundItem)) {
                        player.drop(boundItem, false);
                    }
                }

                var chatComponent = Component.translatable("chat.waystones.scroll_bound", waystone.getName());
                chatComponent.withStyle(ChatFormatting.YELLOW);
                player.displayClientMessage(chatComponent, true);

                world.playSound(null, context.getClickedPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.2f, 2f);
            }

            return !world.isClientSide ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Player player = (Player) entity;
            IWaystone boundTo = getBoundTo(player, stack);
            if (boundTo != null) {
                double distance = entity.distanceToSqr(boundTo.getPos().getX(), boundTo.getPos().getY(), boundTo.getPos().getZ());
                if (distance <= 3.0) {
                    return stack;
                }

                PlayerWaystoneManager.tryTeleportToWaystone(player, boundTo, getWarpMode(), null);
            }
        }

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        IWaystone boundTo = getBoundTo(player, itemStack);
        if (boundTo != null) {
            if (!player.isUsingItem() && world.isClientSide) {
                world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
            }

            if (getUseDuration(itemStack) <= 0 || Compat.isVivecraftInstalled) {
                finishUsingItem(itemStack, world, player);
            } else {
                player.startUsingItem(hand);
            }

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        } else {
            var chatComponent = Component.translatable("chat.waystones.scroll_not_yet_bound");
            chatComponent.withStyle(ChatFormatting.RED);
            player.displayClientMessage(chatComponent, true);
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        Player player = Balm.getProxy().getClientPlayer();
        if (player == null) {
            return;
        }

        IWaystone boundTo = getBoundTo(player, stack);
        MutableComponent targetText = boundTo != null ? Component.literal(boundTo.getName()) : Component.translatable("tooltip.waystones.bound_to_none");
        if (boundTo != null) {
            targetText.withStyle(ChatFormatting.AQUA);
        }

        var boundToText = Component.translatable("tooltip.waystones.bound_to", targetText);
        boundToText.withStyle(ChatFormatting.GRAY);
        list.add(boundToText);
    }

}
