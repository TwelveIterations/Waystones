package net.blay09.mods.waystones.config.rules;

import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.waystones.requirement.ModifierRequirement;
import net.blay09.mods.shogi.common.util.InventoryLookup;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.context.executor.aggregate.AggregateKey;
import net.blay09.mods.shogi.effect.ConstantEffect;
import net.blay09.mods.shogi.effect.EffectArgumentCodecs;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record ModifierCost(HolderSet<Item> item, ShogiEffect<?> count) implements ShogiEffect<ModifierRequirement> {

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("waystones", "modifier_cost");

    public static final AggregateKey<AggregatedModifierCost> AGGREGATE_KEY = AggregateKey.of(IDENTIFIER);

    public static MapCodec<ModifierCost> mapCodec(ShogiScope scope) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false).fieldOf("item").forGetter(ModifierCost::item),
                EffectArgumentCodecs.effectOrConstant(scope).fieldOf("count").orElse(new ConstantEffect(new JsonPrimitive(1))).forGetter(ModifierCost::count)
        ).apply(instance, ModifierCost::new));
    }

    @Override
    public Either<ModifierRequirement, Object> apply(ShogiContext context) {
        final int requestedCount = count.apply(context).mapLeft(Coercion.NON_NEGATIVE_INT).orThrow();

        // We only support the source waystone for this, since we do not have the target waystone chunk loaded at this point.
        final var container = WaystoneRuleContext.getSourceModifierContainer(context).orElse(null);
        if (container == null) {
            return Either.right(new ModifierRequirement(item, 0, requestedCount, true));
        }

        Predicate<ItemStack> matcher = it -> it.is(item);
        final int consumed = context.statefulAggregate(
                AGGREGATE_KEY,
                () -> new AggregatedModifierCost(container),
                state -> state.simulateConsume(matcher, requestedCount)
        );
        final int available = InventoryLookup.countMatchingInContainer(container, matcher);
        if (consumed < requestedCount) {
            return Either.right(new ModifierRequirement(
                    item,
                    available,
                    requestedCount,
                    true
            ));
        }

        context.consume(AGGREGATE_KEY, AggregatedModifierCost::consume);
        return Either.left(new ModifierRequirement(item, available, requestedCount, true));
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    public static final class AggregatedModifierCost {
        private final Container inventory;
        private final SimpleContainer snapshot;
        private final List<CommittableOperation> operations = new ArrayList<>();

        public AggregatedModifierCost(Container inventory) {
            this.inventory = inventory;
            snapshot = new SimpleContainer(inventory.getContainerSize());
            for (int i = 0; i < snapshot.getContainerSize(); i++) {
                snapshot.setItem(i, inventory.getItem(i).copy());
            }
        }

        public int simulateConsume(Predicate<ItemStack> matcher, int count) {
            operations.add(new CommittableOperation(matcher, count));
            return InventoryLookup.consumeFromContainer(snapshot, matcher, count);
        }

        public void consume() {
            for (final var operation : operations) {
                InventoryLookup.consumeFromContainer(inventory, operation.matcher(), operation.count());
            }
        }

        private record CommittableOperation(Predicate<ItemStack> matcher, int count) {
        }
    }
}
