package net.blay09.mods.waystones.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.List;

public class StructureAwareBlockEntityHooks {

    public static void rewriteStructureAwareBlockEntities(Level level, BlockPos origin, Vec3i size, List<StructureTemplate.Palette> palettes) {
        for (final var palette : palettes) {
            final var blocks = palette.blocks();
            for (int i = 0; i < blocks.size(); i++) {
                final var info = blocks.get(i);
                final var blockEntity = level.getBlockEntity(origin.offset(info.pos()));
                if (info.nbt() != null && blockEntity instanceof StructureAwareBlockEntity structureEntity) {
                    final var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
                    blockEntity.saveWithId(output);
                    structureEntity.saveStructureData(output, origin, size);
                    blocks.set(i, new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), output.buildResult()));
                }
            }
        }
    }

}
