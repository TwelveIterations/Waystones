package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.menu.WarpPlateContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrumblingAttunedShardItem extends AbstractAttunedShardItem {

    public CrumblingAttunedShardItem(Properties properties) {
        super(properties.stacksTo(4));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        IWaystone attunedWarpPlate = getWaystoneAttunedTo(null, stack);
        if (attunedWarpPlate != null && attunedWarpPlate.isValid()) {
            var textComponent = Component.translatable("tooltip.waystones.attuned_shard.attunement_crumbling");
            textComponent.withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC);

            Player player = Balm.getProxy().getClientPlayer();
            if (player != null && player.containerMenu instanceof WarpPlateContainer wpc) {
                if (!attunedWarpPlate.getWaystoneUid().equals(wpc.getWaystone().getWaystoneUid())) {
                    list.add(textComponent);
                }
            } else {
                list.add(textComponent);
            }
        }
    }

}
