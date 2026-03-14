package net.blay09.mods.waystones.api.trait;

import net.blay09.mods.waystones.api.SharestoneType;
import org.jspecify.annotations.Nullable;

public interface SharestoneScoped {
    @Nullable
    SharestoneType getSharestoneType();
}
