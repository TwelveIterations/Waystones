package net.blay09.mods.waystones.menu;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.shogi.network.ShogiStreamCodecs;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.config.rules.WaystonesEffectExecutors;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.blay09.mods.waystones.core.WaystoneTeleportContextImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class WaystoneSelectionMenu extends AbstractContainerMenu {

    public record Data(UserDecoratedWaystone fromWaystone, List<UserDecoratedWaystone> waystones, Map<UUID, Either<List<Object>, List<Object>>> warpRequirements) {
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Either<List<Object>, List<Object>>> WARP_REQUIREMENT_STREAM_CODEC = ByteBufCodecs.either(
            ShogiStreamCodecs.LIST_STREAM_CODEC,
            ShogiStreamCodecs.LIST_STREAM_CODEC
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<UUID, Either<List<Object>, List<Object>>>> WARP_REQUIREMENTS_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            UUIDUtil.STREAM_CODEC,
            WARP_REQUIREMENT_STREAM_CODEC
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
            UserDecoratedWaystone.STREAM_CODEC,
            Data::fromWaystone,
            UserDecoratedWaystone.LIST_STREAM_CODEC,
            Data::waystones,
            WARP_REQUIREMENTS_STREAM_CODEC,
            Data::warpRequirements,
            Data::new);

    private final @Nullable Waystone fromWaystone;
    private final List<UserDecoratedWaystone> waystones;
    private final Map<UUID, Either<List<Object>, List<Object>>> warpRequirements;
    private final Set<Identifier> flags;
    private Consumer<WaystoneTeleportContext> postTeleportHandler = _ -> {};
    private ItemStack warpItem = ItemStack.EMPTY;
    private @Nullable InteractionHand warpHand;

    public WaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> type, @Nullable Waystone fromWaystone, int windowId, List<UserDecoratedWaystone> waystones, Map<UUID, Either<List<Object>, List<Object>>> warpRequirements, Set<Identifier> flags) {
        super(type, windowId);
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
        this.warpRequirements = warpRequirements;
        this.flags = flags;
    }

    public WaystoneSelectionMenu withWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    public WaystoneSelectionMenu withHand(InteractionHand hand) {
        this.warpHand = hand;
        return this;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (fromWaystone != null) {
            BlockPos pos = fromWaystone.getPos();
            return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
        }

        return true;
    }

    @Nullable
    public Waystone getWaystoneFrom() {
        return fromWaystone;
    }

    public ItemStack getWarpItem() {
        return warpItem;
    }

    public @Nullable InteractionHand getWarpHand() {
        return warpHand;
    }

    public Collection<UserDecoratedWaystone> getWaystones() {
        return waystones;
    }

    public Set<Identifier> getFlags() {
        return flags;
    }

    public Consumer<WaystoneTeleportContext> getPostTeleportHandler() {
        return postTeleportHandler;
    }

    public WaystoneSelectionMenu setPostTeleportHandler(Consumer<WaystoneTeleportContext> postTeleportHandler) {
        this.postTeleportHandler = postTeleportHandler;
        return this;
    }

    public Either<List<Object>, List<Object>> getWarpRequirements(Waystone waystone) {
        return warpRequirements.getOrDefault(waystone.getWaystoneUid(), Either.left(List.of()));
    }

    public static Map<UUID, Either<List<Object>, List<Object>>> buildWarpRequirements(ServerPlayer player, @Nullable Waystone fromWaystone, List<? extends Waystone> waystones, Set<Identifier> flags) {
        return buildWarpRequirements(player, fromWaystone, waystones, flags, ItemStack.EMPTY, null);
    }

    public static Map<UUID, Either<List<Object>, List<Object>>> buildWarpRequirements(ServerPlayer player, @Nullable Waystone fromWaystone, List<? extends Waystone> waystones, Set<Identifier> flags, ItemStack warpItem, @Nullable InteractionHand warpHand) {
        final var warpRequirements = new HashMap<UUID, Either<List<Object>, List<Object>>>();
        for (final var waystone : waystones) {
            final var context = WaystonesAPI.createUnboundTeleportContext(player, waystone);
            context.setFromWaystone(fromWaystone);
            context.setWarpItem(warpItem);
            if (warpHand != null) {
                context.setWarpHand(warpHand);
            }
            context.addFlags(flags);
            if (context instanceof WaystoneTeleportContextImpl impl) {
                impl.setExecutor(WaystonesEffectExecutors.simulated());
            }
            warpRequirements.put(waystone.getWaystoneUid(), context.getRequirements());
        }
        return warpRequirements;
    }

}
