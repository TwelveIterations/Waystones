package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SharestoneBlockEntity extends WaystoneBlockEntityBase {

    public SharestoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.sharestone.value(), pos, state);
    }

    @Override
    protected Identifier getWaystoneKind() {
        return Optional.of(WaystoneKinds.getKind(((SharestoneBlock) getBlockState().getBlock()).getType()))
                .orElse(WaystoneKinds.WAYSTONE); // fallback to regular waystone if invalid
    }

    @Override
    public void initializeWaystone(ServerLevelAccessor level, @Nullable LivingEntity player, WaystoneOrigin origin) {
        super.initializeWaystone(level, player, origin);

        WaystoneSyncManager.sendWaystoneUpdateToAll(level.getServer(), getWaystone());
    }

    @Override
    public Component getName() {
        return Component.translatable("container.waystones.sharestone");
    }

    @Override
    public Optional<MenuProvider> getSelectionMenuProvider(ServerPlayer player) {
        return Optional.of(new WaystoneSelectionListBuilder(player)
                .withTargetsForWaystone(getWaystone())
                .buildMenuProvider(ModMenus.sharestoneSelection.value(), Component.translatable("container.waystones.waystone_selection")));
    }
}
