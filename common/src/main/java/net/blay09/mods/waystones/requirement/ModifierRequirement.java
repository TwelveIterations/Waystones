package net.blay09.mods.waystones.requirement;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record ModifierRequirement(
        HolderSet<Item> item,
        int available,
        int required,
        boolean consumed
) {
    public String translationKey() {
        return consumed ? "gui.waystones.waystone_selection.modifier_cost"
                : "gui.waystones.waystone_selection.modifier_requirement";
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierRequirement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(Registries.ITEM),
            ModifierRequirement::item,
            ByteBufCodecs.VAR_INT,
            ModifierRequirement::available,
            ByteBufCodecs.VAR_INT,
            ModifierRequirement::required,
            ByteBufCodecs.BOOL,
            ModifierRequirement::consumed,
            ModifierRequirement::new
    );
}
