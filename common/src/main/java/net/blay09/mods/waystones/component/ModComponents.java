package net.blay09.mods.waystones.component;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.component.BalmComponents;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class ModComponents {
    public static DeferredObject<DataComponentType<UUID>> waystone;
    public static DeferredObject<DataComponentType<Component>> waystoneName;
    public static DeferredObject<DataComponentType<UUID>> attunement;

    public static void initialize(BalmComponents components) {
        waystone = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "waystone"));
        waystoneName = components.registerComponent(() -> DataComponentType.<Component>builder().persistent(ComponentSerialization.CODEC).build(), ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "waystone_name"));
        attunement = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "attunement"));
    }
}
