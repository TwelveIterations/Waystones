package net.blay09.mods.waystones.compat;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

/**
 * Kinda awkward but the only way I could easily avoid much duplicate code without having deobf in shared gradle.
 */
public class WaystonesWailaUtils {

    public static final Identifier WAYSTONE_UID = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "waystone");

    public static void appendTooltip(BlockEntity blockEntity, Player player, Consumer<Component> tooltipConsumer) {
        if (blockEntity instanceof WarpPlateBlockEntity warpPlate) {
            Waystone waystone = warpPlate.getWaystone();
            tooltipConsumer.accept(WarpPlateBlock.getGalacticName(waystone.getWaystoneUid()));
        } else if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            Waystone waystone = waystoneBlockEntity.getWaystone();
            boolean isActivated = !waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone);
            if (isActivated && waystone.hasName() && waystone.isValid()) {
                tooltipConsumer.accept(waystone.getName());
            } else {
                tooltipConsumer.accept(Component.translatable("tooltip.waystones.undiscovered"));
            }
        }
    }

    private WaystonesWailaUtils() {
    }

}
