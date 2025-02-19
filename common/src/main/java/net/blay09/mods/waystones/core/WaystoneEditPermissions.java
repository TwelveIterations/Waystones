package net.blay09.mods.waystones.core;

import org.jetbrains.annotations.Nullable;

public enum WaystoneEditPermissions {
    ALLOW(null),
    NOT_CREATIVE(null),
    NOT_THE_OWNER("chat.waystones.only_owner_can_edit"),
    GET_CREATIVE("chat.waystones.only_creative_can_edit");

    private final String langKey;

    WaystoneEditPermissions(@Nullable String langKey) {
        this.langKey = langKey;
    }

    @Nullable
    public String getLangKey() {
        return langKey;
    }
}
