package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.WaystoneReferenceComponent;
import net.blay09.mods.waystones.menu.WaystoneModifierMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Optional;
import java.util.function.Consumer;

public class CrumblingAttunedShardItem extends AbstractAttunedShardItem {

    public CrumblingAttunedShardItem(Properties properties) {
        super(properties.stacksTo(4));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, list, flag);

        final var attunedWaystoneId = Optional.ofNullable(stack.get(ModComponents.warpPlateAttunement.value()))
                .map(WaystoneReferenceComponent::waystoneId)
                .orElseGet(() -> stack.get(ModComponents.attunement.value()));
        if (attunedWaystoneId != null) {
            var textComponent = Component.translatable("tooltip.waystones.attuned_shard.attunement_crumbling");
            textComponent.withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC);

            Player player = Balm.safeClientAccess().getClientPlayer();
            if (player != null && player.containerMenu instanceof WaystoneModifierMenu wpc) {
                if (!attunedWaystoneId.equals(wpc.getWaystone().getWaystoneUid())) {
                    list.accept(textComponent);
                }
            } else {
                list.accept(textComponent);
            }
        }
    }

}
