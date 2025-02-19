package net.blay09.mods.waystones.config;

import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;

import java.util.List;
import java.util.function.Supplier;

public interface IWaystonesConfig {
    boolean restrictToCreative();

    boolean generatedWaystonesUnbreakable();

    boolean globalWaystoneRequiresCreative();

    boolean restrictRenameToOwner();

    boolean transportLeashed();

    List<String> leashedDenyList();

    boolean transportLeashedDimensional();

    DimensionalWarp dimensionalWarp();

    List<String> dimensionalWarpAllowList();

    List<String> dimensionalWarpDenyList();

    boolean allowWaystoneToWaystoneTeleport();

    int inventoryButtonCooldown();

    int warpStoneCooldown();

    double globalWaystoneCooldownMultiplier();

    boolean inverseXpCost();

    int blocksPerXPLevel();

    int dimensionalWarpXpCost();

    double inventoryButtonXpCostMultiplier();

    double warpStoneXpCostMultiplier();

    double sharestoneXpCostMultiplier();

    double waystoneXpCostMultiplier();

    double portstoneXpCostMultiplier();

    double warpPlateXpCostMultiplier();

    double globalWaystoneXpCostMultiplier();

    int xpCostPerLeashed();

    int scrollUseTime();

    int warpStoneUseTime();

    List<String> customWaystoneNames();

    double minimumXpCost();

    double maximumXpCost();

    String inventoryButton();

    default InventoryButtonMode getInventoryButtonMode() {
        return new InventoryButtonMode(inventoryButton());
    }

    boolean disableTextGlow();

    WorldGenStyle worldGenStyle();

    boolean spawnInVillages();

    boolean forceSpawnInVillages();

    NameGenerationMode nameGenerationMode();

    List<String> worldGenDimensionAllowList();

    List<String> worldGenDimensionDenyList();

    int worldGenFrequency();

    int creativeWarpButtonX();
    int creativeWarpButtonY();

    int warpButtonX();
    int warpButtonY();
}
