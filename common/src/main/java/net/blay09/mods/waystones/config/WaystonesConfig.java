package net.blay09.mods.waystones.config;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.blay09.mods.balm.platform.config.reflection.NestedType;
import net.blay09.mods.balm.platform.config.reflection.Synced;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Config(Waystones.MOD_ID)
public class WaystonesConfig {

    public static final List<String> DEFAULT_WARP_SETTINGS = List.of(
            "$xp_per_block = 0.01",
            "$min_xp_cost = 0",
            "$max_xp_cost = 27",
            "$interdimensional_xp_cost = 27",
            "$inventory_button_cooldown = '300s'"
    );

    public static final List<String> DEFAULT_WARP_REQUIREMENTS = List.of(
            "$uses_xp #= and(any(source(is_waystone), source(is_warp_stone)), not(target(is_fleeting_memorial)))",
            "$uses_xp -> $xp_points_cost = $distance * $xp_per_block",
            "$uses_xp + is_interdimensional -> $xp_points_cost = $interdimensional_xp_cost",
            "$uses_xp -> $xp_points_cost = clamp($xp_points_cost, 0, $max_xp_cost)",
            "is_warp_stone -> damage_item(80)",
            "is_inventory_button -> cooldown_cost('inventory_button', $inventory_button_cooldown)"
    );

    public enum TransportMobs implements StringRepresentable {
        ENABLED,
        SAME_DIMENSION,
        DISABLED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum VillageWaystoneGeneration implements StringRepresentable {
        DISABLED,
        REGULAR,
        FREQUENT;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum DefaultWaystoneVisibility implements StringRepresentable {
        ACTIVATION(WaystoneVisibility.ACTIVATION),
        GLOBAL(WaystoneVisibility.GLOBAL);

        private final WaystoneVisibility visibility;

        DefaultWaystoneVisibility(WaystoneVisibility visibility) {
            this.visibility = visibility;
        }

        public WaystoneVisibility getVisibility() {
            return visibility;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public Rules rules = new Rules();
    public InventoryButton inventoryButton = new InventoryButton();
    public WorldGen worldGen = new WorldGen();
    public Client client = new Client();
    public JourneyMap journeyMap = new JourneyMap();
    public BlueMap blueMap = new BlueMap();
    public Dynmap dynmap = new Dynmap();

    public static class Rules {

        @Synced
        @Comment("Set to \"global\" to have newly placed or found waystones be global by default.")
        public DefaultWaystoneVisibility defaultVisibility = DefaultWaystoneVisibility.ACTIVATION;

        @Synced
        @Comment("Set to true to allow everyone to manage global waystones. Always true if `defaultVisibility` is `global`. Can be overridden by Shogi Rule `may_manage_global_waystones`.")
        public boolean allowEveryoneToManageGlobalWaystones = false;

        @Synced
        @Comment("Set to false to simply disable all xp costs. See rules for more fine-grained control.")
        public boolean enableXpCosts = true;

        @Synced
        @Comment("Set to false to simply disable all durability costs. See rules for more fine-grained control.")
        public boolean enableDurability = true;

        @Synced
        @Comment("Set to false to simply disable all cooldowns. See rules for more fine-grained control.")
        public boolean enableCooldowns = true;

        @Synced
        @NestedType(String.class)
        @Comment("List of variables used in the warpRequirements configuration.")
        public List<String> warpSettings = DEFAULT_WARP_SETTINGS;

        @Synced
        @NestedType(String.class)
        @Comment("List of warp requirements in Shogi format.")
        public List<String> warpRequirements = DEFAULT_WARP_REQUIREMENTS;

        @Synced
        @Comment("Set to \"enabled\" to have nearby pets teleport with you. Set to \"same_dimension\" to have nearby pets teleport with you only if you're not changing dimensions. Set to \"disabled\" to disable.")
        public TransportMobs transportPets = TransportMobs.DISABLED;

        @Synced
        @Comment("Set to \"enabled\" to have leashed mobs teleport with you. Set to \"same_dimension\" to have leashed mobs teleport with you only if you're not changing dimensions. Set to \"disabled\" to disable.")
        public TransportMobs transportLeashed = TransportMobs.ENABLED;

        @Comment("List of entities that cannot be teleported, either as pet, leashed, or on warp plates.")
        @NestedType(Identifier.class)
        public Set<Identifier> entityDenyList = Set.of(Identifier.withDefaultNamespace("wither"));

        @Comment("Set to true to enable warp modifier items for applying status effects on teleports.")
        public boolean enableModifiers = true;
    }

    public static class InventoryButton {
        @Synced
        @Comment("Set to 'none' for no inventory button. Set to 'nearest' for an inventory button that teleports to the nearest waystone. Set to 'any' for an inventory button that opens the waystone selection menu. Set to a waystone name for an inventory button that teleports to a specifically named waystone.")
        public String inventoryButton = "";

        @Comment("The x position of the inventory button in the inventory.")
        @Synced
        public int inventoryButtonX = 58;

        @Comment("The y position of the inventory button in the inventory.")
        @Synced
        public int inventoryButtonY = 60;

        @Comment("The y position of the inventory button in the creative menu.")
        @Synced
        public int creativeInventoryButtonX = 88;

        @Comment("The y position of the inventory button in the creative menu.")
        @Synced
        public int creativeInventoryButtonY = 33;
    }

    public static class WorldGen {
        @Comment("Set to 'default' to only generate the normally textured waystones. Set to 'mossy' or 'sandy' to generate all as that variant. Set to 'biome' to make the style depend on the biome it is generated in.")
        public WorldGenStyle wildWaystoneStyle = WorldGenStyle.BIOME;

        @Comment("Approximate chunk distance between wild waystones being generated. Set to 0 to disable generation.")
        public int chunksBetweenWildWaystones = 25;

        @Comment("List of dimensions that wild waystones are allowed to spawn in. If left empty, all dimensions except those in wildWaystonesDimensionDenyList will be able to spawn waystones, provided the biomes are in the `has_structure/waystone` tag.")
        @NestedType(Identifier.class)
        public Set<Identifier> wildWaystonesDimensionAllowList = Set.of(Identifier.withDefaultNamespace("overworld"), Identifier.withDefaultNamespace("the_nether"), Identifier.withDefaultNamespace("the_end"));

        @Comment("List of dimensions that wild waystones are not allowed to spawn in, even if the biome is in the `has_structure/waystone` tag. Only used if wildWaystonesDimensionAllowList is empty.")
        @NestedType(Identifier.class)
        public Set<Identifier> wildWaystonesDimensionDenyList = Set.of();

        @Comment("Set to 'preset_first' to first use names from the nameGenerationPresets. Set to 'preset_only' to use only those custom names. Set to 'mixed' to have some waystones use custom names, and others random names.")
        public NameGenerationMode nameGenerationMode = NameGenerationMode.PRESET_FIRST;

        @Comment("The template to use when generating new names. Supported placeholders are {Biome} (english biome name) and {MrPork} (the default name generator).")
        public String nameGenerationTemplate = "{MrPork}";

        @Comment("These names will be used for the preset name generation mode. See the nameGenerationMode option for more info.")
        @NestedType(String.class)
        public List<String> nameGenerationPresets = List.of();

        @Comment("Set to \"regular\" to have waystones spawn in some villages. Set to \"frequent\" to have waystones spawn in most villages. Set to \"disabled\" to disable waystone generation in villages. Waystones will only spawn in vanilla or supported villages.")
        public VillageWaystoneGeneration spawnInVillages = VillageWaystoneGeneration.REGULAR;
    }

    public static class JourneyMap {
        @Comment("If enabled, JourneyMap waypoints will be created for each activated waystone.")
        public boolean enabled = true;

        @Comment("If enabled, JourneyMap waypoints will only be created if the mod 'JourneyMap Integration' is not installed")
        public boolean preferJourneyMapIntegrationMod = true;
    }

    public static class Dynmap {
        @Comment("If enabled, Waystones will add markers for waystones and sharestones to Dynmap.")
        public boolean enabled = true;
    }

    public static class BlueMap {
        @Comment("Controls whether Waystones' BlueMap integration should be loaded")
        public boolean enabled = true;

        @Comment("If enabled, waystones will be tracked as markers on BlueMap")
        public boolean includeWaystones = true;

        @Comment("If enabled, sharestones will be tracked as markers on BlueMap.")
        public boolean includeSharestones = true;

        @Comment("If enabled, undiscovered waystones will be tracked as markers on BlueMap.")
        public boolean includeUndiscoveredWaystones = false;
    }

    public static class Client {
        @Comment("If enabled, the text overlay on waystones will no longer always render at full brightness.")
        public boolean disableTextGlow = false;
    }

    public InventoryButtonMode getInventoryButtonMode() {
        return new InventoryButtonMode(inventoryButton.inventoryButton);
    }

    public static WaystonesConfig getActive() {
        return Objects.requireNonNull(Balm.config().getActiveConfig(WaystonesConfig.class));
    }
}
