package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class FleetingMemorialBlockEntity extends WaystoneBlockEntityBase {

    private Component ownerName = Component.empty();

    public FleetingMemorialBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.fleetingMemorial.value(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("OwnerName", ComponentSerialization.CODEC, ownerName);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ownerName = input.read("OwnerName", ComponentSerialization.CODEC).orElse(Component.empty());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, output -> {
            output.store("Waystone", WaystoneImpl.CODEC.codec(), getWaystone());
            output.store("OwnerName", ComponentSerialization.CODEC, ownerName);
        });
    }

    @Override
    protected Identifier getWaystoneKind() {
        return WaystoneKinds.FLEETING_MEMORIAL;
    }

    public Component getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(Component ownerName) {
        this.ownerName = ownerName;
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    @Override
    public Component getName() {
        return Component.translatable("container.waystones.fleeting_memorial");
    }
}
