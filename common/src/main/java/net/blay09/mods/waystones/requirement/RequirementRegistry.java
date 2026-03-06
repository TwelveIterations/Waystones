package net.blay09.mods.waystones.requirement;

@Deprecated(forRemoval = true)
public class RequirementRegistry {

    /*public static void registerDefaults() {
        registerModifier("dismount", createDefaultType("dismount", DismountRequirement.class), NoParameter.class, (cost, context, parameters) -> cost, () -> true);

        registerConditionResolver("source_name_equals",
                StringParameter.class,
                (context, parameters) -> context.getFromWaystone().map(waystone -> parameters.value().equals(waystone.getName().getString())).orElse(false));
        registerConditionResolver("source_name_contains",
                StringParameter.class,
                (context, parameters) -> context.getFromWaystone().map(waystone -> waystone.getName().getString().contains(parameters.value())).orElse(false));
        registerBoundConditionResolver("is_interdimensional", NoParameter.class, (context, parameters) -> context.isDimensionalTeleport());
        registerConditionResolver("source_is_warp_plate", NoParameter.class,
                (context, parameters) -> context.getFromWaystone().map(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE)).orElse(false));
        registerConditionResolver("source_is_portstone", NoParameter.class,
                (context, parameters) -> context.getFlags().contains(TeleportFlags.PORTSTONE));
        registerConditionResolver("source_is_waystone", NoParameter.class,
                (context, parameters) -> context.getFromWaystone().map(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)).orElse(false));
        registerConditionResolver("source_is_sharestone", NoParameter.class,
                (context, parameters) -> context.getFromWaystone().map(waystone -> WaystoneTypes.isSharestone(waystone.getWaystoneType())).orElse(false));
        for (final var sharestoneType : WaystoneTypes.SHARESTONES) {
            registerConditionResolver("source_is_" + sharestoneType.getPath(),
                    NoParameter.class,
                    (context, parameters) -> sharestoneType.equals(context.getTargetWaystone().getWaystoneType()));
        }
        registerConditionResolver("source_is_inventory_button",
                NoParameter.class,
                (context, parameters) -> context.getFlags().contains(TeleportFlags.INVENTORY_BUTTON));
        registerConditionResolver("source_is_scroll", NoParameter.class, (context, parameters) -> context.getWarpItem().is(ModItemTags.SCROLLS));
        registerConditionResolver("source_is_bound_scroll", NoParameter.class, (context, parameters) -> context.getWarpItem().is(ModItemTags.BOUND_SCROLLS));
        registerConditionResolver("source_is_return_scroll", NoParameter.class, (context, parameters) -> context.getWarpItem().is(ModItemTags.RETURN_SCROLLS));
        registerConditionResolver("source_is_warp_scroll", NoParameter.class, (context, parameters) -> context.getWarpItem().is(ModItemTags.WARP_SCROLLS));
        registerConditionResolver("source_is_warp_stone", NoParameter.class, (context, parameters) -> context.getWarpItem().is(ModItemTags.WARP_STONES));
        registerBoundConditionResolver("target_name_equals",
                StringParameter.class,
                (context, parameters) -> parameters.value().equals(context.getTargetWaystone().getName().getString()));
        registerBoundConditionResolver("target_name_contains",
                StringParameter.class,
                (context, parameters) -> context.getTargetWaystone().getName().getString().contains(parameters.value()));
        registerBoundConditionResolver("target_is_warp_plate",
                NoParameter.class,
                (context, parameters) -> context.getTargetWaystone().getWaystoneType().equals(WaystoneTypes.WARP_PLATE));
        registerBoundConditionResolver("target_is_global",
                NoParameter.class,
                (context, parameters) -> context.getTargetWaystone().getVisibility() == WaystoneVisibility.GLOBAL);
        registerBoundConditionResolver("target_is_sharestone",
                NoParameter.class,
                (context, parameters) -> WaystoneTypes.isSharestone(context.getTargetWaystone().getWaystoneType()));
        for (final var sharestoneType : WaystoneTypes.SHARESTONES) {
            registerBoundConditionResolver("target_is_" + sharestoneType.getPath(),
                    NoParameter.class,
                    (context, parameters) -> sharestoneType.equals(context.getTargetWaystone().getWaystoneType()));
        }
        registerBoundConditionResolver("target_is_waystone",
                NoParameter.class,
                (context, parameters) -> context.getTargetWaystone().getWaystoneType().equals(WaystoneTypes.WAYSTONE));
        registerConditionResolver("is_on_any_vehicle", NoParameter.class, (context, parameters) -> context.getEntity().getVehicle() != null);
        registerConditionResolver("is_on_vehicle", TaggableIdParameter.class, (context, parameters) -> {
            final var vehicle = context.getEntity().getVehicle();
            if (vehicle != null) {
                if (parameters.isTag()) {
                    return vehicle.typeHolder().is(TagKey.create(Registries.ENTITY_TYPE, parameters.value()));
                } else {
                    return BuiltInRegistries.ENTITY_TYPE.getKey(vehicle.getType()).equals(parameters.value());
                }
            }

            return false;
        });
        registerConditionResolver("is_with_pets",
                NoParameter.class,
                (context, parameters) -> context.getEntity() instanceof LivingEntity livingEntity && !WaystoneTeleportManager.findPets(livingEntity).isEmpty());
        registerConditionResolver("is_with_passengers", NoParameter.class, (context, parameters) -> !WaystoneTeleportManager.findPassengers(context.getEntity()).isEmpty());
        registerConditionResolver("is_with_leashed",
                NoParameter.class,
                (context, parameters) -> !WaystoneTeleportManager.findLeashedAnimals(context.getEntity()).isEmpty());
        registerConditionResolver("source_is_dimension",
                IdParameter.class,
                (context, parameters) -> context.getFromWaystone()
                        .map(waystone -> waystone.getDimension().identifier())
                        .orElseGet(() -> context.getEntity().level().dimension().identifier())
                        .equals(parameters.value));
        registerBoundConditionResolver("target_is_dimension",
                IdParameter.class,
                (context, parameters) -> context.getTargetWaystone().getDimension().identifier().equals(parameters.value));
        registerConditionResolver("involves_dimension",
                IdParameter.class,
                (context, parameters) -> context.getTargetWaystone().getDimension().identifier().equals(parameters.value) || context.getFromWaystone()
                        .map(waystone -> waystone.getDimension().identifier())
                        .orElseGet(() -> context.getEntity().level().dimension().identifier())
                        .equals(parameters.value));
        registerBoundConditionResolver("is_within_distance",
                FloatParameter.class,
                (context, parameters) -> (float) Math.sqrt(context.getEntity()
                        .distanceToSqr(context.getTargetWaystone().getPos().getCenter())) <= parameters.value);
        registerConditionResolver("has_cooldown",
                WaystonesIdParameter.class,
                (context, parameters) -> {
                    if (context.getEntity() instanceof Player player) {
                        return PlayerWaystoneManager.getCooldownMillisLeft(player, parameters.value()) > 0;
                    }
                    return false;
                });
        registerConditionResolver("has_item",
                ItemParameter.class,
                (context, parameters) -> {
                    if (context.getEntity() instanceof Player player) {
                        final var item = BuiltInRegistries.ITEM.getValue(parameters.item().value());
                        return InventoryItemResolver.countMatchingInPlayerInventory(player, stack -> stack.is(item)) >= parameters.count().value();
                    }
                    return false;
                });
        registerConditionResolver("has_empty_inventory",
                NoParameter.class,
                (context, parameters) -> {
                    if (context.getEntity() instanceof Player player) {
                        return player.getInventory().isEmpty();
                    }
                    return true;
                });
        registerConditionResolver("is_wearing_any_armor",
                NoParameter.class,
                (context, parameters) -> {
                    if (context.getEntity() instanceof LivingEntity livingEntity) {
                        for (final var equipmentslot : EquipmentSlotGroup.ARMOR) {
                            if (equipmentslot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                                final var itemstack = livingEntity.getItemBySlot(equipmentslot);
                                if (!itemstack.isEmpty()) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                });
        registerConditionResolver("has_cooldown_above",
                CooldownAboveParameter.class,
                (context, parameters) -> {
                    if (context.getEntity() instanceof Player player) {
                        return PlayerWaystoneManager.getCooldownMillisLeft(player, parameters.cooldown().value()) / 1000f > parameters.seconds().value();
                    }
                    return false;
                });
        registerConditionResolver("has_tag",
                StringParameter.class,
                (context, parameters) -> context.getEntity().entityTags().contains(parameters.value()));

        registerBoundVariableResolver("distance", it -> (float) Math.sqrt(it.getEntity().distanceToSqr(it.getTargetWaystone().getPos().getCenter())));
        registerVariableResolver("leashed", it -> (float) WaystoneTeleportManager.findLeashedAnimals(it.getEntity()).size());
        registerVariableResolver("pets",
                it -> it.getEntity() instanceof LivingEntity livingEntity ? (float) WaystoneTeleportManager.findPets(livingEntity).size() : 0);
        registerVariableResolver("passengers", it -> (float) WaystoneTeleportManager.findPassengers(it.getEntity()).size());
    }*/

}
