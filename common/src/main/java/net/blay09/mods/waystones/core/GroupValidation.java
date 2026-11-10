package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Collection;

public class GroupValidation {

    public static final int MAX_CUSTOM_GROUPS = 64;
    public static final int MAX_NAME_LENGTH = 128;

    public static final StreamCodec<RegistryFriendlyByteBuf, Identifier> IDENTIFIER_CODEC = ByteBufCodecs.stringUtf8(256)
            .map(Identifier::parse, Identifier::toString)
            .cast();

    public static long getCustomGroupCount(Collection<WaystoneGroup> groups) {
        return groups.stream().filter(group -> !group.inbuilt()).count();
    }
}
