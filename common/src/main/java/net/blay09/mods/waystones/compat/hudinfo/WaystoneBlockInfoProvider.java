package net.blay09.mods.waystones.compat.hudinfo;

import net.blay09.mods.balm.platform.compatibility.hudinfo.BlockInfoContext;
import net.blay09.mods.balm.platform.compatibility.hudinfo.BlockInfoProvider;
import net.blay09.mods.balm.platform.compatibility.hudinfo.HudInfoOutput;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.chat.Component;

public class WaystoneBlockInfoProvider implements BlockInfoProvider {
    @Override
    public void apply(BlockInfoContext context, HudInfoOutput output) {
        final var blockEntity = context.blockEntity();
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            final var waystone = waystoneBlockEntity.getWaystone();
            final var isActivated = !waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)
                    || PlayerWaystoneManager.isWaystoneActivated(context.player(), waystone);
            if (isActivated && waystone.hasName() && waystone.isValid()) {
                output.text(waystone.getName());
            } else {
                output.text(Component.translatable("tooltip.waystones.undiscovered"));
            }
        }
    }
}
