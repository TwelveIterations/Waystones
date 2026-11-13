package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.blay09.mods.waystones.component.ModComponents;
import net.minecraft.world.Container;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WaystoneStructureData {
    private boolean structurePlacement;

    public boolean isStructurePlacement() {
        return structurePlacement;
    }

    public void saveForStructure(WaystoneBlockEntityBase blockEntity, Container container, ValueOutput output, BlockPos origin, Vec3i size) {
        output.discard("UUID");
        output.putBoolean("StructurePlacement", true);

        final var level = blockEntity.getLevel();
        final var items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < items.size(); slot++) {
            final var slotStackCopy = container.getItem(slot).copy();
            items.set(slot, slotStackCopy);
            final var target = WaystonesAPI.getBoundWaystone(null, slotStackCopy).orElse(null);
            if (target != null && target.isValid() && target.getDimension().equals(level.dimension())) {
                final var pos = target.getPos();
                if (pos.getX() >= origin.getX() && pos.getX() < origin.getX() + size.getX()
                        && pos.getY() >= origin.getY() && pos.getY() < origin.getY() + size.getY()
                        && pos.getZ() >= origin.getZ() && pos.getZ() < origin.getZ() + size.getZ()
                        && level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase) {
                    slotStackCopy.set(ModComponents.relativePosAttunement.value(), pos.subtract(blockEntity.getBlockPos()));
                    slotStackCopy.remove(ModComponents.absolutePosAttunement.value());
                    slotStackCopy.remove(ModComponents.warpPlateAttunement.value());
                    slotStackCopy.remove(ModComponents.attunement.value());
                }
            }
        }
        output.discard("Items");
        ContainerHelper.saveAllItems(output, items);
    }

    public void save(ValueOutput output) {
        output.putBoolean("StructurePlacement", structurePlacement);
    }

    public void load(ValueInput input) {
        structurePlacement = input.getBooleanOr("StructurePlacement", false);
    }

    public void transform(StructurePlaceSettings settings, WaystoneBlockEntityBase source, Container container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            final var stack = container.getItem(slot);
            final var offset = stack.get(ModComponents.relativePosAttunement.value());
            if (offset != null) {
                final var transformedOffset = StructureTemplate.transform(offset, settings.getMirror(), settings.getRotation(), BlockPos.ZERO);
                stack.set(ModComponents.absolutePosAttunement.value(), GlobalPos.of(source.getLevel().dimension(), source.getBlockPos().offset(transformedOffset)));
                stack.remove(ModComponents.relativePosAttunement.value());
            }
        }
    }
}
