package net.blay09.mods.waystones.component;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.component.BalmComponents;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public class ModComponents {
    public static DeferredObject<DataComponentType<Component>> description;
    public static DeferredObject<DataComponentType<UUID>> waystone;
    public static DeferredObject<DataComponentType<WaystoneNameComponent>> waystoneName;
    public static DeferredObject<DataComponentType<UUID>> attunement;

    public static void initialize(BalmComponents components) {
        description = components.registerComponent(() -> DataComponentType.<Component>builder().persistent(ComponentSerialization.CODEC).build(),
                id("description"));
        waystone = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), id("waystone"));
        waystoneName = components.registerComponent(() -> DataComponentType.<WaystoneNameComponent>builder().persistent(WaystoneNameComponent.CODEC).build(),
                id("waystone_name"));
        attunement = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), id("attunement"));
    }
}
