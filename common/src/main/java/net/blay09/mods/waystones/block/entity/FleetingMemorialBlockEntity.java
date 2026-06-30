package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class FleetingMemorialBlockEntity extends WaystoneBlockEntityBase {

    private Component ownerName = Component.empty();

    public FleetingMemorialBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.fleetingMemorial.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("OwnerName", Component.Serializer.toJson(ownerName, provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("OwnerName")) {
            ownerName = Component.Serializer.fromJson(tag.getString("OwnerName"), provider);
        }
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);
        tag.putString("OwnerName", Component.Serializer.toJson(ownerName, level.registryAccess()));
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.FLEETING_MEMORIAL;
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
