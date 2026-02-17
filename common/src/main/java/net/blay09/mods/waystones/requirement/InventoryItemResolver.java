package net.blay09.mods.waystones.requirement;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryItemResolver {

    private static final int MAX_NESTING_DEPTH = 32;

    public static final List<NestedInventoryComponentHandler> NESTED_COMPONENT_HANDLERS = List.of(
            new NestedInventoryComponentHandler(
                    DataComponents.CONTAINER,
                    parentStack -> {
                        final var containerContents = parentStack.get(DataComponents.CONTAINER);
                        return containerContents != null ? containerContents.nonEmptyItems() : List.of();
                    },
                    parentStack -> {
                        final var containerContents = parentStack.get(DataComponents.CONTAINER);
                        return containerContents != null ? containerContents.allItemsCopyStream().collect(Collectors.toCollection(ArrayList::new)) : List.of();
                    },
                    (parentStack, items) -> parentStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items))
            ),
            new NestedInventoryComponentHandler(
                    DataComponents.BUNDLE_CONTENTS,
                    parentStack -> {
                        final var bundleContents = parentStack.get(DataComponents.BUNDLE_CONTENTS);
                        return bundleContents != null ? bundleContents.items() : List.of();
                    },
                    parentStack -> {
                        final var bundleContents = parentStack.get(DataComponents.BUNDLE_CONTENTS);
                        return bundleContents != null ? bundleContents.itemCopyStream().collect(Collectors.toCollection(ArrayList::new)) : List.of();
                    },
                    (parentStack, items) -> {
                        final var compactedBundleItems = items.stream().filter(it -> !it.isEmpty()).map(ItemStackTemplate::fromNonEmptyStack).toList();
                        parentStack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(compactedBundleItems));
                    }
            )
    );

    public static int countMatchingInPlayerInventory(Player player, Predicate<ItemStack> matcher) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            final var slotStack = player.getInventory().getItem(i);
            if (slotStack.isEmpty()) {
                continue;
            }

            if (matcher.test(slotStack)) {
                count += slotStack.getCount();
            }

            count += countMatchingInNestedContainers(ItemStackTemplate.fromNonEmptyStack(slotStack), matcher, 0);
        }
        return count;
    }

    public static int consumeFromPlayerInventory(Player player, Predicate<ItemStack> matcher, int count) {
        if (count <= 0) {
            return 0;
        }

        int consumed = 0;

        // Consume nonEmptyItems from inventory before checking nested containers
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            final var slotStack = player.getInventory().getItem(i);
            if (slotStack.isEmpty() || !matcher.test(slotStack)) {
                continue;
            }

            final var toConsume = Math.min(slotStack.getCount(), count - consumed);
            slotStack.shrink(toConsume);
            consumed += toConsume;
            if (consumed >= count) {
                player.getInventory().setChanged();
                return consumed;
            }
        }

        // Consume nonEmptyItems from nested containers
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            final var slotStack = player.getInventory().getItem(i);
            if (slotStack.isEmpty()) {
                continue;
            }

            consumed += consumeFromNestedContainers(slotStack, matcher, count - consumed, 0);
            if (consumed >= count) {
                player.getInventory().setChanged();
                return consumed;
            }
        }

        player.getInventory().setChanged();
        return consumed;
    }

    private static int countMatchingInNestedContainers(ItemStackTemplate parentStack, Predicate<ItemStack> matcher, int depth) {
        if (depth >= MAX_NESTING_DEPTH) {
            return 0;
        }

        int count = 0;
        for (final var componentHandler : NESTED_COMPONENT_HANDLERS) {
            for (final var childStack : componentHandler.nonEmptyItems().apply(parentStack)) {
                if (matcher.test(childStack.create())) {
                    count += childStack.count();
                }
                count += countMatchingInNestedContainers(childStack, matcher, depth + 1);
            }
        }

        return count;
    }

    private static int consumeFromNestedContainers(ItemStack parentStack, Predicate<ItemStack> matcher, int count, int depth) {
        if (count <= 0 || depth >= MAX_NESTING_DEPTH) {
            return 0;
        }

        int consumed = 0;
        for (final var componentHandler : NESTED_COMPONENT_HANDLERS) {
            final var componentItems = componentHandler.mutableItems().apply(parentStack);
            boolean changed = false;

            for (final var childStack : componentItems) {
                if (childStack.isEmpty() || !matcher.test(childStack)) {
                    continue;
                }

                final var toConsume = Math.min(childStack.getCount(), count - consumed);
                childStack.shrink(toConsume);
                consumed += toConsume;
                changed = true;
                if (consumed >= count) {
                    break;
                }
            }

            if (consumed < count) {
                for (final var childStack : componentItems) {
                    if (childStack.isEmpty()) {
                        continue;
                    }
                    final var nestedConsumed = consumeFromNestedContainers(childStack, matcher, count - consumed, depth + 1);
                    if (nestedConsumed > 0) {
                        changed = true;
                        consumed += nestedConsumed;
                    }
                    if (consumed >= count) {
                        break;
                    }
                }
            }

            if (changed) {
                componentHandler.writeBack().accept(parentStack, componentItems);
            }

            if (consumed >= count) {
                return consumed;
            }
        }

        return consumed;
    }

    public record NestedInventoryComponentHandler(
            DataComponentType<?> componentType,
            Function<ItemStackTemplate, Iterable<ItemStackTemplate>> nonEmptyItems,
            Function<ItemStack, List<ItemStack>> mutableItems,
            BiConsumer<ItemStack, List<ItemStack>> writeBack
    ) {
    }
}
