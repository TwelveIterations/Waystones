package net.blay09.mods.waystones.component;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.component.BalmComponents;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public class ModComponents {

    @Deprecated(forRemoval = true, since = "1.22")
    public static DeferredObject<DataComponentType<UUID>> waystone;

    @Deprecated(forRemoval = true, since = "1.21.6")
    public static DeferredObject<DataComponentType<WaystoneNameComponent>> waystoneName;

    @Deprecated(forRemoval = true, since = "1.22")
    public static DeferredObject<DataComponentType<UUID>> attunement;

    public static DeferredObject<DataComponentType<DescriptionComponent>> description;
    public static DeferredObject<DataComponentType<BlankScrollComponent>> blankScroll;
    public static DeferredObject<DataComponentType<BoundScrollComponent>> boundScroll;
    public static DeferredObject<DataComponentType<ReturnScrollComponent>> returnScroll;
    public static DeferredObject<DataComponentType<WaystoneReferenceComponent>> waystoneIdentity;
    public static DeferredObject<DataComponentType<WaystoneReferenceComponent>> warpPlateAttunement;

    public static void initialize(BalmComponents components) {
        description = components.registerComponent(() -> DataComponentType.<DescriptionComponent>builder().persistent(DescriptionComponent.CODEC).build(),
                id("description"));
        waystone = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), id("waystone"));
        waystoneName = components.registerComponent(() -> DataComponentType.<WaystoneNameComponent>builder().persistent(WaystoneNameComponent.CODEC).build(),
                id("waystone_name"));
        attunement = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), id("attunement"));

        blankScroll = components.registerComponent(() -> DataComponentType.<BlankScrollComponent>builder().persistent(BlankScrollComponent.CODEC).build(), id("blank_scroll"));
        boundScroll = components.registerComponent(() -> DataComponentType.<BoundScrollComponent>builder().persistent(BoundScrollComponent.CODEC).build(), id("bound_scroll"));
        returnScroll = components.registerComponent(() -> DataComponentType.<ReturnScrollComponent>builder().persistent(ReturnScrollComponent.CODEC).build(), id("return_scroll"));
        waystoneIdentity = components.registerComponent(() -> DataComponentType.<WaystoneReferenceComponent>builder().persistent(WaystoneReferenceComponent.CODEC).build(), id("waystone_identity"));
        warpPlateAttunement = components.registerComponent(() -> DataComponentType.<WaystoneReferenceComponent>builder().persistent(WaystoneReferenceComponent.CODEC).build(), id("waystone_attunement"));
    }
}
