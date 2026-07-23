package net.blay09.mods.waystones.compat.hudinfo;

import net.blay09.mods.balm.platform.compatibility.hudinfo.BlockInfoContext;
import net.blay09.mods.balm.platform.compatibility.hudinfo.BlockInfoProvider;
import net.blay09.mods.balm.platform.compatibility.hudinfo.HudInfoOutput;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;

public class WarpPlateBlockInfoProvider implements BlockInfoProvider {
    @Override
    public void apply(BlockInfoContext context, HudInfoOutput output) {
        final var blockEntity = context.blockEntity();
        if (blockEntity instanceof WarpPlateBlockEntity warpPlate) {
            final var waystone = warpPlate.getWaystone();
            output.text(WarpPlateBlock.getGalacticName(waystone.getWaystoneUid()));
        }
    }
}
