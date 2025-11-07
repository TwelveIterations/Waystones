package net.blay09.mods.waystones.component;

import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;

import java.util.UUID;

public class ModComponents {

    @Deprecated(forRemoval = true, since = "1.22")
    public static Holder<DataComponentType<UUID>> waystone;

    @Deprecated(forRemoval = true, since = "1.21.6")
    public static Holder<DataComponentType<WaystoneNameComponent>> waystoneName;

    @Deprecated(forRemoval = true, since = "1.22")
    public static Holder<DataComponentType<UUID>> attunement;

    public static Holder<DataComponentType<DescriptionComponent>> description;
    public static Holder<DataComponentType<BlankScrollComponent>> blankScroll;
    public static Holder<DataComponentType<BoundScrollComponent>> boundScroll;
    public static Holder<DataComponentType<ReturnScrollComponent>> returnScroll;
    public static Holder<DataComponentType<WaystoneReferenceComponent>> waystoneIdentity;
    public static Holder<DataComponentType<WaystoneReferenceComponent>> warpPlateAttunement;

    public static void initialize(BalmDataComponentTypeRegistrar components) {
        description = components.register("description", DescriptionComponent.CODEC).asHolder();
        waystone = components.register("waystone", UUIDUtil.CODEC).asHolder();
        waystoneName = components.register("waystone_name", WaystoneNameComponent.CODEC).asHolder();
        attunement = components.register("attunement", UUIDUtil.CODEC).asHolder();

        blankScroll = components.register("blank_scroll", BlankScrollComponent.CODEC).asHolder();
        boundScroll = components.register("bound_scroll", BoundScrollComponent.CODEC).asHolder();
        returnScroll = components.register("return_scroll", ReturnScrollComponent.CODEC).asHolder();
        waystoneIdentity = components.register("waystone_identity", WaystoneReferenceComponent.CODEC).asHolder();
        warpPlateAttunement = components.register("waystone_attunement", WaystoneReferenceComponent.CODEC).asHolder();
    }
}
