package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class WaystoneBlockEntity extends WaystoneBlockEntityBase {

    public WaystoneBlockEntity(BlockEntityType<WaystoneBlockEntity> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }

    public WaystoneBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.waystone.value(), blockPos, blockState);
    }

    @Override
    protected Identifier getWaystoneKind() {
        return WaystoneKinds.WAYSTONE;
    }

    @Override
    public Component getName() {
        return Component.translatable("container.waystones.waystone");
    }

    @Override
    public Optional<MenuProvider> getSelectionMenuProvider(ServerPlayer player) {
        return Optional.of(new WaystoneSelectionListBuilder(player)
                .withTargetsForWaystone(getWaystone())
                .buildMenuProvider(ModMenus.waystoneSelection.value(), Component.translatable("container.waystones.waystone_selection")));
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
