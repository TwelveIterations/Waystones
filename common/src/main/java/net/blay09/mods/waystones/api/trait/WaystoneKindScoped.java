package net.blay09.mods.waystones.api.trait;

import net.minecraft.resources.Identifier;

@Deprecated // TODO This would be better as an Item Component?
public interface WaystoneKindScoped {
    Identifier getWaystoneKind();
}
