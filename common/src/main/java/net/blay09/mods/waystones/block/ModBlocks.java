package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

public class ModBlocks {

    private static final DyeColor[] portstoneColors = new DyeColor[]{
            DyeColor.WHITE,
            DyeColor.ORANGE,
            DyeColor.MAGENTA,
            DyeColor.LIGHT_BLUE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.PINK,
            DyeColor.GRAY,
            DyeColor.LIGHT_GRAY,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK
    };

    private static final DyeColor[] sharestoneColors = new DyeColor[]{
            DyeColor.ORANGE,
            DyeColor.MAGENTA,
            DyeColor.LIGHT_BLUE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.PINK,
            DyeColor.GRAY,
            DyeColor.LIGHT_GRAY,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK
    };

    public static Block waystone;
    public static Block mossyWaystone;
    public static Block sandyWaystone;
    public static Block deepslateWaystone;
    public static Block blackstoneWaystone;
    public static Block endStoneWaystone;
    public static Block warpPlate;
    public static Block warpPortal;
    public static Block fleetingMemorial;
    public static final PortstoneBlock[] portstones = new PortstoneBlock[portstoneColors.length];
    public static final SharestoneBlock[] sharestones = new SharestoneBlock[sharestoneColors.length];

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> waystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(waystone), id("waystone"));
        blocks.register(() -> mossyWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(mossyWaystone), id("mossy_waystone"));
        blocks.register(() -> sandyWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(sandyWaystone), id("sandy_waystone"));
        blocks.register(() -> deepslateWaystone = new WaystoneBlock(defaultProperties().sound(SoundType.DEEPSLATE)),
                () -> itemBlock(deepslateWaystone),
                id("deepslate_waystone"));
        blocks.register(() -> blackstoneWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(blackstoneWaystone), id("blackstone_waystone"));
        blocks.register(() -> endStoneWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(endStoneWaystone), id("end_stone_waystone"));
        blocks.register(() -> warpPlate = new WarpPlateBlock(defaultProperties()), () -> itemBlock(warpPlate), id("warp_plate"));
        blocks.registerBlock(() -> warpPortal = new WarpPortalBlock(defaultProperties().noLootTable().lightLevel(state -> 10).strength(-1f, 3600000f)), id("warp_portal"));
        blocks.registerBlock(() -> fleetingMemorial = new FleetingMemorialBlock(defaultProperties().noLootTable().strength(2f, 6f).lightLevel(state -> 6)), id("fleeting_memorial"));

        for (final var color : portstoneColors) {
            blocks.register(() -> portstones[color.ordinal()] = new PortstoneBlock(color, defaultProperties()),
                    () -> itemBlock(portstones[color.ordinal()]),
                    id(color.getSerializedName() + "_portstone"));
        }

        for (final var color : sharestoneColors) {
            blocks.register(() -> sharestones[color.ordinal() - 1] = new SharestoneBlock(color, defaultProperties()),
                    () -> itemBlock(sharestones[color.ordinal() - 1]),
                    id(color.getSerializedName() + "_sharestone"));
        }
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties().sound(SoundType.STONE).strength(5f, 2000f).requiresCorrectToolForDrops();
    }

    @Nullable
    public static SharestoneBlock getSharestone(DyeColor color) {
        final var index = color.ordinal() - 1; // -1 because we skip WHITE
        if (index < 0 || index >= sharestones.length) {
            return null;
        }

        return sharestones[index];
    }

    @Nullable
    public static PortstoneBlock getPortstone(DyeColor color) {
        final var index = color.ordinal();
        if (index >= portstones.length) {
            return null;
        }

        return portstones[index];
    }

}
