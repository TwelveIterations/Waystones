package net.blay09.mods.waystones.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.storage.ValueOutput;

public interface StructureAwareBlockEntity {
    default void saveStructureData(ValueOutput output, BlockPos origin, Vec3i size) {
    }

    default void loadStructureData(StructurePlaceSettings settings) {
    }
}
