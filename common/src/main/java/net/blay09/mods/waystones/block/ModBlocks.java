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

public class ModBlocks {

    private static final DyeColor[] sharestoneColors = new DyeColor[]{
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

    public static Block waystone;
    public static Block mossyWaystone;
    public static Block sandyWaystone;
    public static Block deepslateWaystone;
    public static Block blackstoneWaystone;
    public static Block endStoneWaystone;
    public static Block sharestone;
    public static Block warpPlate;
    public static Block portstone;
    public static Block[] scopedSharestones = new SharestoneBlock[sharestoneColors.length];

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> waystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(waystone), id("waystone"));
        blocks.register(() -> mossyWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(mossyWaystone), id("mossy_waystone"));
        blocks.register(() -> sandyWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(sandyWaystone), id("sandy_waystone"));
        blocks.register(() -> deepslateWaystone = new WaystoneBlock(defaultProperties().sound(SoundType.DEEPSLATE)),
                () -> itemBlock(deepslateWaystone),
                id("deepslate_waystone"));
        blocks.register(() -> blackstoneWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(blackstoneWaystone), id("blackstone_waystone"));
        blocks.register(() -> endStoneWaystone = new WaystoneBlock(defaultProperties()), () -> itemBlock(endStoneWaystone), id("end_stone_waystone"));
        blocks.register(() -> sharestone = new SharestoneBlock(defaultProperties(), null), () -> itemBlock(sharestone), id("sharestone"));
        blocks.register(() -> warpPlate = new WarpPlateBlock(defaultProperties()), () -> itemBlock(warpPlate), id("warp_plate"));
        blocks.register(() -> portstone = new PortstoneBlock(defaultProperties()), () -> itemBlock(portstone), id("portstone"));

        for (DyeColor color : sharestoneColors) {
            blocks.register(() -> scopedSharestones[color.ordinal()] = new SharestoneBlock(defaultProperties(), color), () -> itemBlock(scopedSharestones[color.ordinal()]), id(color.getSerializedName() + "_sharestone"));
        }
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties().sound(SoundType.STONE).strength(5f, 2000f).requiresCorrectToolForDrops();
    }
}
