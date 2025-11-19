package net.blay09.mods.waystones.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PortstoneBlockEntity extends BlockEntity {

    public PortstoneBlockEntity(BlockPos worldPosition, BlockState state) {
        super(ModBlockEntities.portstone.value(), worldPosition, state);
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);
    }
    
}
