package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.mixin.ItemPropertiesAccessor;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void initialize() {
        Balm.getEvents().onEvent(ClientStartedEvent.class, event -> {
            ItemPropertiesAccessor.callRegister(ModItems.twinboundFeather, ResourceLocation.withDefaultNamespace("brushing"), (itemStack, level, entity, seed) -> entity != null && entity.getUseItem() == itemStack
                    ? (float) (entity.getUseItemRemainingTicks() % 10) / 10f
                    : 0f);
        });
    }
}
