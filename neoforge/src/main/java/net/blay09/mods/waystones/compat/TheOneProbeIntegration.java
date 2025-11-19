package net.blay09.mods.waystones.compat;

import mcjty.theoneprobe.api.*;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TheOneProbeIntegration {

    public TheOneProbeIntegration() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopInitializer::new);
    }

    public static class TopInitializer implements Function<ITheOneProbe, Void> {
        @Nullable
        @Override
        public Void apply(@Nullable ITheOneProbe top) {
            if (top != null) {
                top.registerProvider(new ProbeInfoProvider());
            }
            return null;
        }
    }

    public static class ProbeInfoProvider implements IProbeInfoProvider {

        @Override
        public Identifier getID() {
            return Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "top");
        }

        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData data) {
            BlockEntity tileEntity = level.getBlockEntity(data.getPos());
            //noinspection StatementWithEmptyBody
            if (tileEntity instanceof WarpPlateBlockEntity) {
                /* TOP does not use the correct galactic font, so don't display for warp plates.
                IWaystone waystone = ((WarpPlateTileEntity) tileEntity).getWaystone();
                ITextComponent galacticName = WarpPlateBlock.getGalacticName(waystone);
                info.text(galacticName); */
            } else if (tileEntity instanceof WaystoneBlockEntityBase) {
                Waystone waystone = ((WaystoneBlockEntityBase) tileEntity).getWaystone();
                boolean isActivated = !waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone);
                if (isActivated && waystone.hasName() && waystone.isValid()) {
                    info.text(waystone.getName());
                } else {
                    info.text(Component.translatable("tooltip.waystones.undiscovered"));
                }
            }
        }

    }

}
