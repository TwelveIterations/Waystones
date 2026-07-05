package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SharestoneBlockEntity extends WaystoneBlockEntityBase {

    public SharestoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.sharestone.get(), pos, state);
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.getSharestone(((SharestoneBlock) getBlockState().getBlock()).getColor())
                .orElse(WaystoneTypes.WAYSTONE); // fallback to regular waystone if invalid
    }

    @Override
    public void initializeWaystone(ServerLevelAccessor world, @Nullable LivingEntity player, WaystoneOrigin origin) {
        super.initializeWaystone(world, player, origin);

        WaystoneSyncManager.sendWaystoneUpdateToAll(world.getServer(), getWaystone());
    }

    @Override
    public Component getName() {
        return Component.translatable("container.waystones.sharestone");
    }

    @Override
    public Optional<MenuProvider> getSelectionMenuProvider(ServerPlayer player) {
        return Optional.of(new WaystoneSelectionListBuilder(player)
                .withTargetsForWaystone(getWaystone())
                .buildMenuProvider(ModMenus.sharestoneSelection.get(), Component.translatable("container.waystones.waystone_selection")));
    }
}
