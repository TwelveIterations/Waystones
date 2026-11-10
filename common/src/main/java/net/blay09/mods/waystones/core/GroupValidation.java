package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.WaystoneGroup;

import java.util.Collection;

public class GroupValidation {

    public static final int MAX_CUSTOM_GROUPS = 64;
    public static final int MAX_NAME_LENGTH = 128;

    public static final int MAX_IDENTIFIER_LENGTH = 256;

    public static long getCustomGroupCount(Collection<WaystoneGroup> groups) {
        return groups.stream().filter(group -> !group.inbuilt()).count();
    }
}
